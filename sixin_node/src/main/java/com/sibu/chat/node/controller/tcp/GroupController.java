package com.sibu.chat.node.controller.tcp;

import io.netty.channel.ChannelHandlerContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.node.service.interfaces.IGroupService;

/**
 * 群组控制层。
 * @author caishiyu
 */
@Component
public class GroupController {
	@Autowired
	private IGroupService groupService;

	/**
	 * 建立群
	 * @param newGroupIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String newGroup(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return groupService.newGroup(op, in, ctx);
	}

	/**
	 * 加入群
	 * @param joinGroupIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String joinGroup(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return groupService.joinGroup(op, in, ctx);
	}

	/**
	 * 退出群
	 * @param exitGroupIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String exitGroup(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return groupService.exitGroup(op, in, ctx);
	}

	/**
	 * 解散群
	 * @param dropGroupIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String dropGroup(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return groupService.dropGroup(op, in, ctx);
	}

	/**
	 * 修改群名称
	 * @param alterGroupIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String alterGroup(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return groupService.alterGroup(op, in, ctx);
	}

	/**
	 * 邀请用户进群
	 * @param inviteToGIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String inviteToG(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return groupService.inviteToG(op, in, ctx);
	}

	/**
	 * 用户被T出群
	 * @param kickoutIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String kickout(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return groupService.kickout(op, in, ctx);
	}

	/**
	 * 获取群信息
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String getGroups(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return groupService.getGroups(op, in, ctx);
	}

	/**
	 * 获取群用户
	 * @param getGUsersIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String getGUsers(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return groupService.getGUsers(op, in, ctx);
	}

	public String getGById(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return groupService.getGById(op, in, ctx);
	}
}
