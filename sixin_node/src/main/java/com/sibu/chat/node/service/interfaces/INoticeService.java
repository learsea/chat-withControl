package com.sibu.chat.node.service.interfaces;

import io.netty.channel.ChannelHandlerContext;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.bean.po.Notice;
import com.sibu.chat.common.bean.po.Record;
import com.sibu.chat.common.constant.Operation;

/**
 * 服务层。
 * @author caishiyu
 *
 */
public interface INoticeService {

	/**
	 * 获取未读通知
	 * @param in 
	 * @param getOfflineMsgIV 传入参数
	 * @param ctx tcp上下文
	 * @return 返回消息
	 */
	public String getOfflineNtc(Operation op, JSONObject in, ChannelHandlerContext ctx);

	/**
	 * 发送普通通知
	 * @param uid 通知接收者id
	 * @param notice 通知实例
	 * @param needSave 对方不在线时是否需要保存通知
	 */
	void notice(int uid, Notice notice, boolean needSave);

	/**
	 * 发送聊天通知
	 * @param uid 通知接收者id
	 * @param record 聊天实例，保存使用
	 * @param ntc 通知实例，通知使用
	 * @param needSave 是否需要保存聊天记录
	 * @return 对方是否收到聊天消息
	 */
	boolean notice(int uid, Record record, JSONObject ntc, boolean needSave);
}
