package com.sibu.chat.node.service;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.bean.po.User;
import com.sibu.chat.common.bean.vo.tcp.TcpErrorResult;
import com.sibu.chat.common.bean.vo.tcp.TcpNoticeResult;
import com.sibu.chat.common.bean.vo.tcp.TcpNoticeResult.Msg;
import com.sibu.chat.common.bean.vo.tcp.TcpSuccessResult;
import com.sibu.chat.common.constant.ClientType;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.exception.BusinessException;
import com.sibu.chat.common.utils.DateUtils;
import com.sibu.chat.common.utils.JSONUtils;
import com.sibu.chat.node.cache.CtxUser;
import com.sibu.chat.node.dao.NoticeDao;
import com.sibu.chat.node.dao.UserDao;
import com.sibu.chat.node.redis.RUserDao;
import com.sibu.chat.node.service.interfaces.INoticeService;
import com.sibu.chat.node.service.interfaces.IUserService;
import com.sibu.chat.node.utils.TranMsgUtil;

/**
 * 用户服务层。
 * @author caishiyu
 *
 */
@Service
public class UserService implements IUserService {
	private Logger logger = Logger.getLogger(UserService.class);
	@Autowired
	private UserDao userDao;
	@Autowired
	private NoticeDao noticeDao;
	@Autowired
	private RUserDao ruserDao;
	@Autowired
	private INoticeService noticeService;

	@Transactional
	@Override
	public String login(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		int loginId = -1;
		try {
			loginId = in.getIntValue("id");
		} catch (Exception e) {
			throw new BusinessException("请传递id");
		}
		String name = in.getString("name");
		if (name == null) {
			throw new BusinessException("请传递name");
		}

		String appName = in.getString("appName");
		String clientType = null;
		try {
			clientType = ClientType.valueOf(in.getString(ClientType.clientType.toString())).toString();
		} catch (Exception e) {
			return JSONUtils.parse(TcpErrorResult.getResult(op, "参数错误，clientType"));
		}

		try {
			// 根据本次连接获取user
			User cacheUser = CtxUser.getUserByCtx(ctx);
			// 本次连接没有登录
			if (cacheUser == null) {
				// 检查登录用户是否在本节点别的连接登录过
				if (CtxUser.hasUser(loginId)) {
					// 设置vo
					ChannelHandlerContext oldCtx = CtxUser.clearCache(loginId);
					JSONObject ntc = TcpNoticeResult.getResult(Msg.anotherUser);
					oldCtx.writeAndFlush(JSONUtils.parse(ntc)).addListener(ChannelFutureListener.CLOSE);
				}
				// 检查登录用户是否在其他节点登录过
				String address = ruserDao.getUserAddress(loginId);
				if (address != null) {
					// 用户通过别的连接登录过
					// 设置挤下线通知
					JSONObject ntc = TcpNoticeResult.getResult(Msg.anotherUser);
					TranMsgUtil.tran(address, loginId, ntc.toJSONString(), true);
				}

				// 在本节点登录
				cacheUser = new User();
				cacheUser.setId(loginId);
				cacheUser.setName(name);
				cacheUser.setAppName(appName);
				cacheUser.setClientType(clientType);
				String deviceToken = in.getString("deviceToken");
				cacheUser.setDeviceToken(deviceToken);
				cacheUser.setCtx(ctx);
				cacheUser.setLoginTime(DateUtils.getTimeStamp());
				// 登录后需要推送
				cacheUser.setNeedPush(true);
				// 设置缓存
				CtxUser.addCache(cacheUser);
				try {
					userDao.saveUser(cacheUser);
				} catch (DuplicateKeyException e) {
					// 用户登录过，跟新昵称、登录时间、token,app_name等
					userDao.updateUser(cacheUser);
					e.printStackTrace();
				}
			} else {
				// 本次连接登录过，重置返回值中的id
				loginId = cacheUser.getId();
			}
			logger.info("用户：" + loginId + " 登录");

			// 拼接返回结果
			JSONObject result = TcpSuccessResult.getResult(op);
			result.put("id", loginId);
			return JSONUtils.parse(result);
		} catch (Exception e) {
			CtxUser.clearCache(loginId);
			throw new BusinessException(e.getMessage());
		}
	}

	@Transactional
	@Override
	public String logOut(Operation op, JSONObject in, ChannelHandlerContext ctx) {

		// 校验是否登录
		User cacheUser = UserService.checkUser(ctx);
		// 注销后不需要继续推送
		User daoUser = new User();
		daoUser.setId(cacheUser.getId());
		daoUser.setNeedPush(false);
		userDao.updateUser(daoUser);
		CtxUser.clearCache(cacheUser.getId());
		logger.info("用户：" + cacheUser.getId() + " 注销");

		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(op);
		ctx.writeAndFlush(JSONUtils.parse(result)).addListener(ChannelFutureListener.CLOSE);
		return null;
	}

	@Transactional
	@Override
	public String badge(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		checkUser(ctx);
		int badge = JSONUtils.getInt(in, "badge");
		int uid = -1;
		try {
			uid = in.getIntValue("uid");
		} catch (Exception e) {
			throw new BusinessException("请传入uid");
		}
		if (badge == -1) {
			throw new BusinessException("没有传递count、uid");
		}
		User user = new User();
		user.setId(uid);
		user.setBadge(badge);
		userDao.updateUser(user);
		return null;
	}

	@Transactional
	@Override
	public String needPush(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		int uid = -1;
		try {
			uid = in.getIntValue("uid");
		} catch (Exception e) {
			throw new BusinessException("请传入uid");
		}
		User user = new User();
		user.setId(uid);
		user.setNeedPush(in.getBoolean("needPush"));
		userDao.updateUser(user);
		ctx.close();
		return null;
	}

	/**
	 * 检验用户是否登录
	 * @param ctx tcp连接
	 * @return 若用户已经登录则返回user,否则抛出异常
	 */
	public static User checkUser(ChannelHandlerContext ctx) {
		// 根据本次连接获取user
		User cacheUser = CtxUser.getUserByCtx(ctx);
		if (cacheUser == null) {
			throw new BusinessException("没有登录");
		}
		return cacheUser;
	}
}
