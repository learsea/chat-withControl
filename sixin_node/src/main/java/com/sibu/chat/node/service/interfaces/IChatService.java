package com.sibu.chat.node.service.interfaces;

import io.netty.channel.ChannelHandlerContext;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Operation;

/**
 * 服务层。
 * @author caishiyu
 *
 */
public interface IChatService {
	/**
	 * 聊天。
	 * @param ChatIV 传入参数
	 * @param ctx tcp连接上下文
	 * @return 返回信息
	 */
	public String chat(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 群聊
	 * @param in 传入参数
	 * @param ctx tcp连接上下文
	 * @return 返回信息
	 */
	String groupChat(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 获取未读消息
	 * @param in 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String getOfflineMsg(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 撤销单聊消息
	 */
	public String recall(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 撤销群聊消息
	 */
	public String recallG(Operation op, JSONObject in, ChannelHandlerContext ctx);

}
