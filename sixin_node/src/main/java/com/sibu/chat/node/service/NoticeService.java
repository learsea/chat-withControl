package com.sibu.chat.node.service;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.bean.po.Notice;
import com.sibu.chat.common.bean.po.Record;
import com.sibu.chat.common.bean.po.User;
import com.sibu.chat.common.bean.vo.tcp.TcpNoticeResult;
import com.sibu.chat.common.bean.vo.tcp.TcpSuccessResult;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.utils.JSONUtils;
import com.sibu.chat.node.cache.CtxUser;
import com.sibu.chat.node.dao.ChatDao;
import com.sibu.chat.node.dao.NoticeDao;
import com.sibu.chat.node.redis.RUserDao;
import com.sibu.chat.node.service.interfaces.INoticeService;
import com.sibu.chat.node.utils.TranMsgUtil;

@Service
public class NoticeService implements INoticeService {
	@Autowired
	private NoticeDao noticeDao;
	@Autowired
	private RUserDao ruserDao;
	@Autowired
	private ChatDao chatDao;

	@Override
	@Transactional
	public String getOfflineNtc(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		User cacheUser = UserService.checkUser(ctx);
		List<Notice> notices = noticeDao.getNoticesByUserId(cacheUser.getId());
		noticeDao.delNoticeByUserId(cacheUser.getId());
		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("notices", notices);
		return JSONUtils.parse(result);
	}

	@Transactional
	@Override
	public void notice(int uid, Notice notice, boolean needSave) {
		User user = CtxUser.getUserById(uid);
		// 拼接通知
		JSONObject ntc = TcpNoticeResult.getResult(notice);
		String resultStr = JSONUtils.parse(ntc);
		// 用户在本节点登录，直接发送通知
		if (user != null) {
			user.getCtx().writeAndFlush(resultStr);
		} else {
			String address = ruserDao.getUserAddress(uid);
			if (address != null) {
				// 用户在其他节点登录,转发通知
				TranMsgUtil.tran(address, uid, ntc.toJSONString(), false);
			} else {
				if (needSave) {
					// 用户不在线，将通知保存到数据库
					noticeDao.save(notice);
				}
			}
		}
	}

	@Transactional
	@Override
	public boolean notice(int uid, Record record, JSONObject ntc, boolean needSave) {
		// 发送消息
		ChannelHandlerContext listenerCtx = CtxUser.getCtxById(uid);
		if (listenerCtx == null) {
			// 对方不在本节点
			String userAddress = ruserDao.getUserAddress(uid);
			if (userAddress != null) {
				// 对方在其他节点
				TranMsgUtil.tran(userAddress, uid, ntc.toJSONString(), false);
				return true;
			}
			// 用户没登录并且需要保存消息
			if (needSave) {
				chatDao.saveRecord(record);
			}
			return false;
		} else {
			// 对方在线，直接发出消息
			listenerCtx.writeAndFlush(JSONUtils.parse(ntc));
			return true;
		}
	}
}
