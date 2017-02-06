package com.sibu.chat.node.service.interfaces;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.bean.po.Notice;
import com.sibu.chat.common.constant.Operation;

public interface IGroupService {
	/**
	 * 加入群
	 * @param in 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String joinGroup(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 建立群
	 * @param in 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String newGroup(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 退出群
	 * @param in 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String exitGroup(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 解散群
	 * @param in 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String dropGroup(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 修改群信息
	 * @param in 传入参数
	 * @param ctx tcp上下文
	 * @return 返回信息
	 */
	String alterGroup(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 用户被T出群
	 * @param in 传入参数
	 * @param ctx tcp上下文
	 * @return 返回信息
	 */
	public String kickout(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 邀请用户进群
	 * @param in 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String inviteToG(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 获取群信息
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String getGroups(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 获取群用户
	 * @param in 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String getGUsers(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 根据群id获取群信息
	 */
	public String getGById(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 通知群用户
	 * @param loginId 登录用户id
	 * @param userIdList 群用户id列表
	 * @param notice 通知类
	 */
	void noticeGroupUsers(int loginId, List<?> userIdList, Notice notice);
}
