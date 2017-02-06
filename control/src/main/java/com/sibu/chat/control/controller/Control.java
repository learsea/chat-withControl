package com.sibu.chat.control.controller;

import io.netty.channel.ChannelHandlerContext;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.bean.vo.tcp.TcpErrorResult;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.utils.FlashCrossUtils;
import com.sibu.chat.common.utils.JSONUtils;
import com.sibu.chat.control.service.ServerService;
import com.sibu.chat.control.service.UserService;

public class Control {

	/**
	 * 接收参数，分发消息
	 * @param msg 	接收到的原始信息
	 * @param user	tcp连接中保存的用户bean
	 * @param ctx 	tcp连接上下文
	 * @return 返回消息
	 */
	public static String control(String msg, ChannelHandlerContext ctx) {

		if (FlashCrossUtils.FLASHREQUEST.equals(msg)) {
			return FlashCrossUtils.FLASH_RESPONSE + "\0";
		}
		try {
			JSONObject in = null;
			try {
				in = JSONObject.parseObject(msg);
				if (in == null) {
					throw new RuntimeException();
				}
			} catch (Exception e) {
				return JSONUtils.parse(TcpErrorResult.getResult(Operation.unknown, "客户端错误。原因：参数错误"));
			}
			Operation op = null;
			try {
				op = Operation.valueOf(in.getString(Operation.operation.toString()));
			} catch (Exception e) {
				return JSONUtils.parse(TcpErrorResult.getResult(Operation.unknown, "客户端错误。原因：没有传入操作类型,字段operation"));
			}
			String appName = in.getString("appName");
			switch (op) {
			case chooseServer:
				if (appName == null) {
					return JSONUtils.parse(TcpErrorResult.getResult(op, "客户端错误。原因：没有传入appName"));
				}
				return ServerService.chooseServer(appName, ctx);
			case addServer:
				if (appName == null) {
					return JSONUtils.parse(TcpErrorResult.getResult(op, "客户端错误。原因：没有传入appName"));
				}
				return ServerService.addServer(in, ctx);
			case connect:
				return UserService.connect(ctx);
			case disconnect:
				return UserService.disconnect(ctx);
			default:
				return JSONUtils.parse(TcpErrorResult.getResult(op, "客户端错误。原因：未知操作类型"));
			}
		} catch (Exception e) {
			return JSONUtils.parse(TcpErrorResult.getResult(Operation.unknown, "服务器出错"));
		}
	}
}
