package com.sibu.chat.node.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

import com.sibu.chat.node.GlobeConfig;
import com.sibu.chat.node.client.handler.NodeHandler;

/**
 * 当服务器做为节点时的客户端初始化类
 * @author pxw
 *
 */
public class NodeInit implements Runnable {
	@Override
	public void run() {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class)
				.remoteAddress(new InetSocketAddress(GlobeConfig.CENTER_HOST, GlobeConfig.CENTER_PORT))
				.handler(new ChannelInitializer<SocketChannel>() {
					ByteBuf delimiter = Unpooled.copiedBuffer("\0".getBytes());

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new StringEncoder(),
								new DelimiterBasedFrameDecoder(1024 * 1024, delimiter), new StringDecoder(),
								new NodeHandler());
					}
				});

		try {
			try {
				ChannelFuture future = bootstrap.connect().sync();
				future.channel().closeFuture().sync();
			} catch (InterruptedException e) {
			}
		} finally {
			group.shutdownGracefully();
		}
	}
}
