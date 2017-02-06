package com.sibu.chat.node.controller.tcp;

import io.netty.channel.ChannelHandlerContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.node.service.interfaces.IChatService;

/**
 * 聊天控制层。
 * @author caishiyu
 */
@Component
public class ChatController {
	@Autowired
	private IChatService chatService;

	/**
	 * 聊天
	 * @param chatIV 传入参数
	 * @param ctx tcp连接上下文
	 * @return 返回消息
	 */
	public String chat(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return chatService.chat(op, in, ctx);
	}

	/**
	 * 群聊
	 * @param groupChatIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String groupChat(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return chatService.groupChat(op, in, ctx);
	}

	/**
	 * 获取未读消息
	 * @param getOfflineMsgIV 入参
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String getOfflineMsg(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return chatService.getOfflineMsg(op, in, ctx);
	}

	/**
	 * 撤销单聊消息
	 */
	public String recall(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return chatService.recall(op, in, ctx);
	}

	/**
	 * 撤销群聊消息
	 */
	public String recallG(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return chatService.recallG(op, in, ctx);
	}
}
