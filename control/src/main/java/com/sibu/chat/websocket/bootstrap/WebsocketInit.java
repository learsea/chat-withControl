package com.sibu.chat.websocket.bootstrap;

import org.apache.log4j.Logger;

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

import com.sibu.chat.websocket.bootstrap.handler.MsgCodec;
import com.sibu.chat.websocket.bootstrap.handler.WebSocketMsgHandler;

/**
 * @author caishiyu
 */
public class WebsocketInit implements Runnable {
	private Logger logger = Logger.getLogger(WebsocketInit.class);
	private int port;

	public WebsocketInit(int port) {
		this.port = port;
	}

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
						// 编解码http请求
						pipeline.addLast(new HttpServerCodec());
						// 保证收到请求的完整性
						pipeline.addLast(new HttpObjectAggregator(64 * 1024));
						pipeline.addLast(new ChunkedWriteHandler());
						// 处理websocket
						pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
						pipeline.addLast(new MsgCodec());
						pipeline.addLast(new WebSocketMsgHandler());
					}
				});
		try {
			ChannelFuture future = bootstrap.bind(port).syncUninterruptibly();
			logger.info("信息：websocket server startup success");
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
		} finally {
			// 关闭netty
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
