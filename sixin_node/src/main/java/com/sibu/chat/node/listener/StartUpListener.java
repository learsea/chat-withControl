package com.sibu.chat.node.listener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.stereotype.Component;

import com.sibu.chat.common.utils.ThreadPool;
import com.sibu.chat.node.client.cache.ClientCtxCache;
import com.sibu.chat.node.main.Main;
import com.sibu.chat.node.utils.HttpClientUtil;

@Component
public class StartUpListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// 关闭线程池
		ThreadPool.shutdownNow();
		// 断开节点与中控的连接
		ClientCtxCache.clientCtx.close();

		// 销毁httpclient
		try {
			HttpClientUtil.CLIENT.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Main.doInit();
	}
}
