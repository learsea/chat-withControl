package com.sibu.chat.node.controller.tcp;

import io.netty.channel.ChannelHandlerContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.node.service.interfaces.IUserService;

/**
 * 用户控制层。
 * @author caishiyu
 */
@Component
public class UserController {
	@Autowired
	private IUserService userService;

	/**
	 * 登录
	 * @param loginIV 传入参数
	 * @param ctx tcp连接上下文
	 * @return 返回消息
	 */
	public String login(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return userService.login(op, in, ctx);
	}

	/**
	 * 注销
	 * @param logoutIV 传入参数
	 * @param ctx tcp连接上下文
	 * @return 返回消息
	 */
	public String logout(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return userService.logOut(op, in, ctx);
	}

	/**
	 * 减少用户推送气泡数
	 * @return
	 */
	public String badge(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return userService.badge(op, in, ctx);
	}

	/**
	 * 设置用户是否需要推送
	 */
	public String needPush(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return userService.needPush(op, in, ctx);
	}

}
