package com.sibu.chat.node.bootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;

import com.sibu.chat.node.GlobeConfig;
import com.sibu.chat.node.bootstrap.handler.ServiceHandler;

public class ServerInit implements Runnable {
	private Logger logger = Logger.getLogger(ServerInit.class);

	@Override
	public void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				// 设置连接超时时间30秒
				.option(ChannelOption.SO_TIMEOUT, 30)
				// 不使用Nagle算法
				.option(ChannelOption.TCP_NODELAY, true)
				// 使用内存池
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						Charset charset = Charset.forName("utf-8");
						ByteBuf delimiter = Unpooled.copiedBuffer("\0".getBytes());
						// 所有outhandler必须在inhandler前面，以保证在in中输出时能经过所有outhandler
						ch.pipeline().addLast(new StringEncoder(charset),
						// 单次接收最多1K数据
								new DelimiterBasedFrameDecoder(1024 * 1024, delimiter), new StringDecoder(charset),
								// 添加心跳包，每45秒检测一次连接
								new IdleStateHandler(140, 45, 45), new ServiceHandler());
					}
				});
		try {
			ChannelFuture future = bootstrap.bind(GlobeConfig.SERVER_PORT).sync();
			logger.info("信息：chat server init success");
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
		} finally {
			// 关闭netty
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
