package com.sibu.chat.websocket.bootstrap.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import com.sibu.chat.control.controller.Control;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class WebSocketMsgHandler extends SimpleChannelInboundHandler<String> {

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// 调用control方法解析消息
		String response = Control.control(msg, ctx);
		if (response != null) {
			// response为null说明已经处理过输出（比如logout中，关闭了连接）
			ctx.writeAndFlush(response);
		}
	}
}
