package com.sibu.chat.node.controller.tcp;

import io.netty.channel.ChannelHandlerContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.node.service.interfaces.INoticeService;

/**
 * 通知消息控制层。
 * @author caishiyu
 */
@Component
public class NoticeController {
	@Autowired
	private INoticeService noticeService;

	/**
	 * 获取未读通知
	 * @param inVo 
	 * @param getOfflineMsgIV 入参
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String getOfflineNtc(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		return noticeService.getOfflineNtc(op, in, ctx);
	}
}
