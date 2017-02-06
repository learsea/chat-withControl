package com.sibu.chat.websocket.bootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import org.apache.log4j.Logger;

import com.sibu.chat.node.GlobeConfig;
import com.sibu.chat.websocket.bootstrap.handler.MsgCodec;
import com.sibu.chat.websocket.bootstrap.handler.WebSocketMsgHandler;

/**
 * @author caishiyu
 */
public class WebsocketInit implements Runnable {
	private Logger logger = Logger.getLogger(WebsocketInit.class);

	@Override
	public void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new HttpServerCodec());
						pipeline.addLast(new HttpObjectAggregator(64 * 1024));
						pipeline.addLast(new ChunkedWriteHandler());
						pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
						pipeline.addLast(new MsgCodec());
						// 添加心跳包，每45秒检测一次连接
						pipeline.addLast(new IdleStateHandler(140, 45, 45));
						pipeline.addLast(new WebSocketMsgHandler());
					}
				});
		try {
			ChannelFuture future = bootstrap.bind(GlobeConfig.WEBSOCKET_PORT).syncUninterruptibly();
			logger.info("信息：websocket server init success");
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
		} finally {
			// 关闭netty
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
