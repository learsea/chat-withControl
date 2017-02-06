package com.sibu.chat.node.main;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.dbay.apns4j.utils.APNSUtils;
import com.sibu.chat.common.utils.ThreadPool;
import com.sibu.chat.node.bootstrap.ServerInit;
import com.sibu.chat.node.bootstrap.handler.ServiceHandler;
import com.sibu.chat.node.cache.CtxUser;
import com.sibu.chat.node.client.NodeInit;
import com.sibu.chat.websocket.bootstrap.WebsocketInit;

/**
 * 服务启动类
 * @author caishiyu
 */
@Component
public class Main implements ApplicationContextAware {
	private static Logger logger = Logger.getLogger(Main.class);
	public static ApplicationContext context = null;

	/** 服务初始化 */
	public static void doInit() {
		logger.info("信息：starting server");

		APNSUtils.initAPNS();
		// 开启与中控服务器连接的线程
		ThreadPool.getExecutorService().execute(new NodeInit());
		// 开启tcp聊天服务
		ThreadPool.getExecutorService().execute(new ServerInit());
		// 开启websocket聊天服务
		ThreadPool.getExecutorService().execute(new WebsocketInit());

		// debug模式下，记录连接数和登录数
		if (logger.isDebugEnabled()) {
			log();
		}
	}

	private static void log() {
		ThreadPool.getScheduledExecutorService().scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				logger.debug("当前连接数：" + ServiceHandler.conNum.get() + "，当前登录数：" + CtxUser.usersCount());
			}
		}, 60, 60, TimeUnit.SECONDS);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
}