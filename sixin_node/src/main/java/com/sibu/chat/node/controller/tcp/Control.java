package com.sibu.chat.node.controller.tcp;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.bean.po.User;
import com.sibu.chat.common.bean.vo.tcp.TcpErrorResult;
import com.sibu.chat.common.bean.vo.tcp.TcpNoticeResult;
import com.sibu.chat.common.bean.vo.tcp.TcpNoticeResult.Msg;
import com.sibu.chat.common.constant.AppName;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.utils.FlashCrossUtils;
import com.sibu.chat.common.utils.JSONUtils;
import com.sibu.chat.node.cache.CtxUser;

@Component
public class Control {
	@Autowired
	private UserController userController;
	@Autowired
	private ChatController chatController;
	@Autowired
	private GroupController groupController;
	@Autowired
	private FriendController friendController;
	@Autowired
	private NoticeController noticeController;
	private Logger logger = Logger.getLogger(Control.class);

	/**
	 * 接收参数，分发消息
	 * 
	 * @param msg 	接收到的原始信息
	 * @param user	tcp连接中保存的用户bean
	 * @param ctx 	tcp连接上下文
	 * @return 返回消息
	 */
	public String control(String msg, ChannelHandlerContext ctx) {

		if (FlashCrossUtils.FLASHREQUEST.equals(msg)) {
			return FlashCrossUtils.FLASH_RESPONSE + "\0";
		}
		JSONObject in = null;
		try {
			in = JSONObject.parseObject(msg);
			if (in == null) {
				throw new RuntimeException();
			}
		} catch (Exception e) {
			return JSONUtils.parse(TcpErrorResult.getResult(Operation.unknown, "客户端错误。原因：参数错误"));
		}
		Operation op = null;
		try {
			op = Operation.valueOf(in.getString(Operation.operation.toString()));
		} catch (Exception e) {
			return JSONUtils.parse(TcpErrorResult.getResult(Operation.unknown, "客户端错误。原因：没有传入操作类型,字段operation"));
		}
		try {
			AppName.valueOf(in.getString(AppName.appName.toString()));
		} catch (Exception e) {
			return JSONUtils.parse(TcpErrorResult.getResult(op, "客户端错误。原因：没有传入项目名称，字段appName"));
		}
		switch (op) {
		case login:
			return userController.login(op, in, ctx);
		case logout:
			return userController.logout(op, in, ctx);
		case chat:
			return chatController.chat(op, in, ctx);
		case recall:
			return chatController.recall(op, in, ctx);
		case recallG:
			return chatController.recallG(op, in, ctx);
		case newGroup:
			return groupController.newGroup(op, in, ctx);
		case getGroups:
			return groupController.getGroups(op, in, ctx);
		case getGById:
			return groupController.getGById(op, in, ctx);
		case getGUsers:
			return groupController.getGUsers(op, in, ctx);
		case joinGroup:
			return groupController.joinGroup(op, in, ctx);
		case exitGroup:
			return groupController.exitGroup(op, in, ctx);
		case dropGroup:
			return groupController.dropGroup(op, in, ctx);
		case groupChat:
			return chatController.groupChat(op, in, ctx);
		case getOfflineMsg:
			return chatController.getOfflineMsg(op, in, ctx);
		case getOfflineNtc:
			return noticeController.getOfflineNtc(op, in, ctx);
		case alterGroup:
			return groupController.alterGroup(op, in, ctx);
		case inviteToG:
			return groupController.inviteToG(op, in, ctx);
		case addFriend:
			return friendController.addFriend(op, in, ctx);
		case delF:
			return friendController.delF(op, in, ctx);
		case black:
			return friendController.black(op, in, ctx);
		case aggreF:
			return friendController.aggreF(op, in, ctx);
		case kickout:
			return groupController.kickout(op, in, ctx);
		case badge:
			return userController.badge(op, in, ctx);
		case needPush:
			return userController.needPush(op, in, ctx);
		default:
			return JSONUtils.parse(TcpErrorResult.getResult(op, "客户端错误。原因：未知操作类型"));
		}
	}

	/**
	 * 客户端关闭连接
	 * @param ctx tcp连接上下文
	 */
	public void exit(ChannelHandlerContext ctx) {
		User loginUser = CtxUser.getUserByCtx(ctx);
		if (loginUser != null) {
			CtxUser.clearCache(loginUser.getId());
			if (logger.isDebugEnabled()) {
				logger.debug("用户" + loginUser.getId() + "的连接关闭\n");
			}
			loginUser = null;
			JSONObject ntc = TcpNoticeResult.getResult(Msg.timeout);
			ctx.writeAndFlush(JSONUtils.parse(ntc));
		}
		ctx.close();
	}
}
