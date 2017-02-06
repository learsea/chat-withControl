package com.sibu.chat.node.service.interfaces;

import io.netty.channel.ChannelHandlerContext;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Operation;

public interface IUserService {

	/**
	 * 登录。
	 * @param LoginIV 传入参数
	 * @param user tcp连接对应的用户bean
	 * @param ctx tcp连接上下文
	 * @return 返回信息
	 */
	public String login(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 注销。
	 * @param ctx tcp连接上下文
	 * @return 返回信息
	 */
	public String logOut(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 设置当前气泡数量
	 * @param in 传入参数
	 * @param ctx tcp上下文
	 * @return null
	 */
	public String badge(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 设置用户是否需要推送
	 * @param in 传入参数 
	 * @param ctx tcp上下文
	 * @return null
	 */
	public String needPush(Operation op, JSONObject in, ChannelHandlerContext ctx);
}
