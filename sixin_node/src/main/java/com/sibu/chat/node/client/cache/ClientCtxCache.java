package com.sibu.chat.node.client.cache;

import io.netty.channel.ChannelHandlerContext;

/**
 * 保存服务器做为客户端连接其他服务器时的ctx
 * @author pxw
 *
 */
public class ClientCtxCache {
	public static ChannelHandlerContext clientCtx = null;
}
