package com.sibu.chat.node.controller.tcp;

import io.netty.channel.ChannelHandlerContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.node.service.interfaces.IFriendService;

/**
 * 用户是否同意的控制层。
 * @author caishiyu
 */
@Component
public class FriendController {
	@Autowired
	private IFriendService friendService;

	/**
	 * 用户同意添加好友
	 * @param aggreFOrNoIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String aggreF(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return friendService.aggreF(op, in, ctx);
	}

	/**
	 * 删除好友
	 */
	public String delF(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return friendService.delF(op, in, ctx);
	}

	/**
	 * 添加好友
	 */
	public String addFriend(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return friendService.delF(op, in, ctx);
	}

	/**
	 * 添加黑名单
	 */
	public String black(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return friendService.delF(op, in, ctx);
	}

}
