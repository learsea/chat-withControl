package com.sibu.chat.websocket.bootstrap.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.apache.log4j.Logger;

import com.sibu.chat.common.constant.Ping;
import com.sibu.chat.node.controller.tcp.Control;
import com.sibu.chat.node.main.Main;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class WebSocketMsgHandler extends SimpleChannelInboundHandler<String> {
	private Logger logger = Logger.getLogger(WebSocketMsgHandler.class);
	private Control control = Main.context.getBean(Control.class);

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		if (msg.equals(Ping.PING)) {
			return;
		}
		// 调用control方法解析消息
		String response = control.control(msg, ctx);
		if (response != null) {
			// response为null说明已经处理过输出（比如logout中，关闭了连接）
			ctx.writeAndFlush(response);
		}
	}

	/* 心跳检测 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			if (evt instanceof IdleStateEvent) {
				IdleStateEvent e = (IdleStateEvent) evt;
				if (e.state().equals(IdleState.ALL_IDLE)) {
					ctx.writeAndFlush(Ping.PING0);
				} else if (e.state().equals(IdleState.READER_IDLE)) {
					control.exit(ctx);
				}
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("连接关闭");
		}
		super.channelInactive(ctx);
	}
}
