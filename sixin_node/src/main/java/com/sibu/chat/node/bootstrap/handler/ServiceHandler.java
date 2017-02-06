package com.sibu.chat.node.bootstrap.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.sibu.chat.common.bean.vo.tcp.TcpErrorResult;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.constant.Ping;
import com.sibu.chat.common.utils.JSONUtils;
import com.sibu.chat.node.client.cache.ClientCtxCache;
import com.sibu.chat.node.controller.tcp.Control;
import com.sibu.chat.node.main.Main;

/**
 * 总控制类
 * 
 * @author caishiyu
 *
 */
public class ServiceHandler extends ChannelInboundHandlerAdapter {
	private Logger logger = Logger.getLogger(ServiceHandler.class);
	public static AtomicInteger conNum = new AtomicInteger(0);
	private Control control = Main.context.getBean(Control.class);;

	/**
	 * 读消息
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg.equals(Ping.PING)) {
			return;
		}
		try {
			// 调用control方法解析消息
			String response = control.control((String) msg, ctx);
			if (response != null) {
				// response为null说明已经处理过输出（比如logout中，关闭了连接）
				ctx.writeAndFlush(response);
			}
			// 如果是空暂时不处理,由client来处理
		} finally {
			ReferenceCountUtil.release(msg);
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
			ctx.writeAndFlush(JSONUtils.parse(TcpErrorResult.getResult(Operation.unknown,
					"服务器异常。原因：" + cause.getMessage())));
			super.exceptionCaught(ctx, cause);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		conNum.addAndGet(1);
		if (logger.isDebugEnabled()) {
			logger.debug("有新的连接：" + ((InetSocketAddress) ctx.channel().remoteAddress()).getHostName());
		}
		super.channelActive(ctx);
		ChannelHandlerContext centerCtx = ClientCtxCache.clientCtx;
		centerCtx.writeAndFlush("{\"operation\":\"connect\"}\0");
	}

	/** 实例释放, 当Socket连接被用户主动关闭后调用 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		conNum.addAndGet(-1);
		ChannelHandlerContext centerCtx = ClientCtxCache.clientCtx;
		centerCtx.writeAndFlush("{\"operation\":\"disconnect\"}\0");
		if (logger.isDebugEnabled()) {
			logger.debug(((InetSocketAddress) ctx.channel().remoteAddress()).getHostName() + "：断开连接");
		}
		super.channelInactive(ctx);
		control.exit(ctx);
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
}