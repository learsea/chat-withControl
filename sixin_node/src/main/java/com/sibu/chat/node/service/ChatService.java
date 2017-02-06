package com.sibu.chat.node.service;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dbay.apns4j.utils.APNSUtils;
import com.dbay.apns4j.utils.APNSUtils.ApnsType;
import com.sibu.chat.common.bean.po.Notice;
import com.sibu.chat.common.bean.po.Record;
import com.sibu.chat.common.bean.po.User;
import com.sibu.chat.common.bean.vo.tcp.TcpNoticeResult;
import com.sibu.chat.common.bean.vo.tcp.TcpSuccessResult;
import com.sibu.chat.common.constant.AppName;
import com.sibu.chat.common.constant.ClientType;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.constant.Type;
import com.sibu.chat.common.exception.BusinessException;
import com.sibu.chat.common.utils.DateUtils;
import com.sibu.chat.common.utils.JSONUtils;
import com.sibu.chat.node.cache.MsgNum;
import com.sibu.chat.node.dao.ChatDao;
import com.sibu.chat.node.dao.FriendDao;
import com.sibu.chat.node.dao.GroupDao;
import com.sibu.chat.node.dao.UserDao;
import com.sibu.chat.node.redis.RUserDao;
import com.sibu.chat.node.service.interfaces.IChatService;
import com.sibu.chat.node.service.interfaces.IGroupService;
import com.sibu.chat.node.service.interfaces.INoticeService;

/**
 * 聊天服务层。
 * @author caishiyu
 *
 */
@Service
public class ChatService implements IChatService {
	private Logger logger = Logger.getLogger(ChatService.class);
	@Autowired
	private ChatDao chatDao;
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RUserDao ruserDao;
	@Autowired
	private INoticeService noticeService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private FriendDao friendDao;

	@Override
	public String chat(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		User cacheUser = UserService.checkUser(ctx);
		int loginId = cacheUser.getId();
		int listenerId = -1;
		try {
			listenerId = in.getIntValue("listenerId");
		} catch (Exception e) {
			throw new BusinessException("参数格式错误,检查是否传递了listenerId");
		}
		String msgId = in.getString("msgId");

		String content = in.getString("content");

		String dataPush = checkDataPush(loginId, listenerId, in.getString("dataPush"));
		long now = DateUtils.getTimeStamp();
		logger.info("用户：" + loginId + " 向 " + "用户：" + listenerId + " 发送消息： " + content);

		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("time", now);
		result.put("msgId", in.getString("msgId"));

		Record record = new Record(loginId, listenerId, now, content, msgId);
		/* 设置推送参数 */
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", ApnsType.chat);
		params.put("uid", listenerId);

		// 拼接通知
		JSONObject ntc = TcpNoticeResult.getResult(record, Type.chat);
		chatDao.saveRecordLog(record);
		if (sendMsg(record, ntc, listenerId, dataPush, params, true)) {
			result.put("receive", 1);
		} else {
			result.put("receive", 0);
		}
		// 设置已发消息数量
		setMsgNum(AppName.valueOf(in.getString(AppName.appName.toString())));
		return JSONUtils.parse(result);
	}

	@Override
	public String groupChat(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		User cacheUser = UserService.checkUser(ctx);
		String content = in.getString("content");
		int groupId = JSONUtils.getInt(in, "groupId");
		String msgId = in.getString("msgId");
		if (content == null || groupId == 0) {
			throw new BusinessException("参数格式错误,检查是否传递了content");
		}
		List<Integer> groupUsers = groupDao.getGroupUsesIdsByGroupId(groupId);
		if (groupUsers == null || groupUsers.size() < 1) {
			throw new BusinessException("没有该群");
		}
		// 除去自己
		if (!groupUsers.remove(cacheUser.getId())) {
			throw new BusinessException("不是群成员");
		}
		long now = DateUtils.getTimeStamp();
		Record record = new Record(cacheUser.getId(), null, now, content, msgId);
		record.setGroupId(groupId);

		/* 设置推送参数 */
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", ApnsType.chat);
		chatDao.saveRecordLog(record);
		String dataPush = in.getString("dataPush");
		for (int listenerId : groupUsers) {
			String finalDataPush = checkDataPush(cacheUser.getId(), listenerId, dataPush);
			record.setListenerId(listenerId);
			params.put("uid", listenerId);
			// 拼接通知
			JSONObject ntc = TcpNoticeResult.getResult(record, Type.groupChat);
			sendMsg(record, ntc, listenerId, finalDataPush, params, true);
		}
		logger.info("用户：" + cacheUser.getId() + " 在群 " + groupId + " 中发送消息：" + content);

		// 设置已发消息数量
		setMsgNum(AppName.valueOf(in.getString(AppName.appName.toString())));
		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("time", now);
		result.put("msgId", in.getString("msgId"));
		return JSONUtils.parse(result);
	}

	// 设置已发消息数量
	private void setMsgNum(AppName appName) {
		switch (appName) {
		case SiXin:
			MsgNum.SIXIN_MSG_NUM.addAndGet(1);
			return;
		default:
			return;
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public String getOfflineMsg(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		User cacheUser = UserService.checkUser(ctx);
		int listenerId = cacheUser.getId();
		int requestO2oNum = JSONUtils.getInt(in, "one2oneNum");
		int requestGroupNum = JSONUtils.getInt(in, "groupNum");
		// 设置请求条数默认值
		if (requestO2oNum == 0) {
			requestO2oNum = 100;
		}
		if (requestGroupNum == 0) {
			requestGroupNum = 100;
		}
		// 获取未读单聊信息条数
		int o2oNum = chatDao.getOne2OneRecordsCountByUserId(listenerId);
		// 获取未读群聊信息条数
		int groupNum = chatDao.getGroupRecordsCountByUserId(listenerId);
		// 获取未读单聊消息列表
		List<Record> o2oRecords = chatDao.getLimitOne2OneRecordsByUserId(listenerId, requestO2oNum);
		// 删除查看过的单聊消息
		int delO2O = chatDao.delLimitOne2OneRecordsByUserId(listenerId, requestO2oNum);
		// 获取未读群聊消息列表
		List<Record> groupRecords = chatDao.getLimitGroupRecordsByUserId(listenerId, requestGroupNum);
		// 删除查看过的群聊消息
		int delGroup = chatDao.delLimitGroupRecordsByUserId(listenerId, requestGroupNum);
		int o2oRemain = o2oNum - delO2O;
		int groupReamin = groupNum - delGroup;
		logger.info("用户：" + cacheUser.getId() + "获取" + delO2O + "条单聊消息和" + delGroup + "条群消息。剩余" + o2oRemain
				+ "条离线单聊消息和" + groupReamin + "条离线群消息");

		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("one2oneRecords", o2oRecords);
		result.put("one2oneRemain", o2oRemain);
		result.put("groupRecords", groupRecords);
		result.put("groupRemain", groupReamin);
		return JSONUtils.parse(result);
	}

	@Override
	public String recall(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		User cacheUser = UserService.checkUser(ctx);
		String msgId = in.getString("msgId");
		if (msgId == null || msgId.equals("")) {
			throw new BusinessException("msgId为空");
		}
		int loginId = cacheUser.getId();
		int listenerId = -1;
		try {
			listenerId = in.getIntValue("listenerId");
		} catch (Exception e) {
			throw new BusinessException("listenerId错误");
		}
		if (chatDao.recall(loginId, listenerId, msgId) < 1) {
			// 消息已经发出去，通知客户端撤销
			Notice notice = new Notice();
			notice.setCreatetime(DateUtils.getTimeStamp());
			notice.setType(Type.recall.toString());
			notice.setOperatorUid(loginId);
			notice.setUserId(listenerId);
			notice.setMsgId(msgId);
			// 发送通知
			noticeService.notice(listenerId, notice, true);
		}
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("msgId", msgId);
		return JSONUtils.parse(result);
	}

	@Override
	public String recallG(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		User cacheUser = UserService.checkUser(ctx);
		int loginId = cacheUser.getId();
		String msgId = in.getString("msgId");
		if (msgId == null || msgId.equals("")) {
			throw new BusinessException("msgId为空");
		}
		int groupId = 0;
		try {
			in.getIntValue("gid");
		} catch (Exception e) {
			throw new BusinessException("gid不正确");
		}
		chatDao.recallG(loginId, groupId, msgId);
		// 通知群成员撤销消息
		List<Integer> guIds = groupDao.getGroupUsesIdsByGroupId(groupId);
		Notice notice = new Notice();
		notice.setCreatetime(DateUtils.getTimeStamp());
		notice.setType(Type.recall.toString());
		notice.setOperatorUid(loginId);
		notice.setMsgId(msgId);
		// 发送通知
		groupService.noticeGroupUsers(loginId, guIds, notice);
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("msgId", msgId);
		return JSONUtils.parse(result);
	}

	/**
	 * 发送消息
	 * @param record 消息记录
	 * @param listenerId 接收人id
	 * @param needSave 是否需要保存消息到数据库
	 * @return 对方是否在线
	 */
	@Transactional
	public boolean sendMsg(Record record, JSONObject ntc, int listenerId, String dataPush, Map<String, Object> params,
			boolean needSave) {
		// ios消息推送
		User user = userDao.getUserById(listenerId);
		if (user != null && user.getClientType().equals(ClientType.ios.toString()) && user.getNeedPush()) {
			// 用户是登录状态才需要推送
			APNSUtils.noticeByAppName(user.getAppName(), user.getDev(), user.getDeviceToken(), dataPush,
					user.getBadge(), params);
			user.setBadge(user.getBadge() + 1);
			userDao.updateUser(user);
		}
		return noticeService.notice(listenerId, record, ntc, needSave);
	}

	/**
	 * 校验dataPush，超过50长度时，加...，即53长度
	 * @param dataPush 推送内容
	 * @return
	 */
	private String checkDataPush(int tid, int lid, String dataPush) {
		// 查询备注
		String name = friendDao.getRemark(tid, lid);
		if (name == null || name.equals("")) {
			// 查询自己的昵称
			name = userDao.getSinXinName(tid);
		}
		dataPush = name + ":" + dataPush;
		if (dataPush.length() > 53) {
			dataPush = dataPush.substring(0, 50) + "...";
		}
		return dataPush;
	}
}
