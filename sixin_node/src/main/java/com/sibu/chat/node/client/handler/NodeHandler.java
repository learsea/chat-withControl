package com.sibu.chat.node.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.bean.vo.tcp.TcpSuccessResult;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.constant.Ping;
import com.sibu.chat.common.utils.JSONUtils;
import com.sibu.chat.node.GlobeConfig;
import com.sibu.chat.node.client.cache.ClientCtxCache;

public class NodeHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = Logger.getLogger(NodeHandler.class);

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		logger.info("与中控服务器连接断开");
		// 中控断开后节点不再使用
		System.exit(0);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("消息：与中控服务器建立连接成功");
		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(Operation.addServer);
		result.put("host", GlobeConfig.SERVER_HOST);
		result.put("port", GlobeConfig.SERVER_PORT);
		result.put("websocketPort", GlobeConfig.WEBSOCKET_PORT);
		result.put("appName", GlobeConfig.serverType);
		result.put("limitCount", GlobeConfig.SERVER_LIMITCOUNT);
		ClientCtxCache.clientCtx = ctx;
		ctx.writeAndFlush(JSONUtils.parse(result));
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg.equals(Ping.PING)) {
			ctx.writeAndFlush(Ping.PING0);
			if (logger.isDebugEnabled()) {
				logger.debug("收到中控ping");
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause.getMessage().equals("远程主机强迫关闭了一个现有的连接。")) {
			// 不输出异常信息
		} else {
			super.exceptionCaught(ctx, cause);
		}
	}
}
