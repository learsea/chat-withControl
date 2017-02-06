package com.sibu.chat.node.service.interfaces;

import io.netty.channel.ChannelHandlerContext;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Operation;

public interface IFriendService {
	/**
	 * 用户同意添加好友
	 * @param aggreFOrNoIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	String aggreF(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 添加好友
	 * @param in 传入参数
	 * @param ctx tcp连接上下文
	 * @return 返回消息
	 */
	public String addFriend(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 删除好友
	 */
	public String delF(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 添加黑名单
	 */
	public String black(Operation op, JSONObject in, ChannelHandlerContext ctx);
}
