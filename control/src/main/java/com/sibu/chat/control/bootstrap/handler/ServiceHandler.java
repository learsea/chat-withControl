package com.sibu.chat.control.bootstrap.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import org.apache.log4j.Logger;

import com.sibu.chat.common.bean.vo.tcp.TcpErrorResult;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.constant.Ping;
import com.sibu.chat.common.utils.JSONUtils;
import com.sibu.chat.control.bean.pojo.CtxServer;
import com.sibu.chat.control.cache.CtxServerCache;
import com.sibu.chat.control.controller.Control;
import com.sibu.chat.control.utils.SendMsgUtil;

/**
 * 总控制类
 * 
 * @author caishiyu
 *
 */
public class ServiceHandler extends ChannelInboundHandlerAdapter {
	private static Logger logger = Logger.getLogger(ServiceHandler.class);

	/**
	 * 读消息
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg.equals(Ping.PING)) {
			if (logger.isDebugEnabled()) {
				logger.debug("收到节点ping");
			}
			return;
		}
		try {
			// 调用control方法解析消息
			String response = Control.control((String) msg, ctx);
			if (response != null) {
				// response为null说明已经处理过输出（比如chooseServer,logout中，关闭了连接）
				ctx.writeAndFlush(response);
			}
		} finally {
			ReferenceCountUtil.release(msg);
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
					ctx.close();
					logger.warn("中控断开与节点的连接");
				}
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		CtxServer server = CtxServerCache.getCtxServerByCtx(ctx);
		if (server != null) {
			int count = CtxServerCache.delServer(server);
			logger.info("节点" + server.getHost() + "断开连接，剩余节点数为" + count);
			SendMsgUtil.sendMsg(server, "已宕机");
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("断开客户端连接");
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// 如果收到内容太多无法解析，则通知客户端
		if (cause instanceof TooLongFrameException) {
			ctx.writeAndFlush(JSONUtils.parse(TcpErrorResult.getResult(Operation.unknown, "客户端错误。原因：内容过多，无法解析")));
		} else if (cause.getMessage().equals("远程主机强迫关闭了一个现有的连接。")
				|| cause.getMessage().equals("Connection reset by peer")) {
			// 不输出异常信息
		} else {
			super.exceptionCaught(ctx, cause);
		}
	}
}