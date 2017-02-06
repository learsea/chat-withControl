package com.sibu.chat.control.main;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sibu.chat.common.utils.ThreadPool;
import com.sibu.chat.control.bootstrap.ServerInit;
import com.sibu.chat.control.redis.RUserDao;
import com.sibu.chat.websocket.bootstrap.WebsocketInit;

/**
 * 中控服务启动类
 * @author caishiyu
 */
public class Main {
	private static Logger logger = Logger.getLogger(ServerInit.class);
	// log4j配置文件路径
	private static final String log4jPath = "conf/log/log4j.properties";

	public static ClassPathXmlApplicationContext context = null;
	// spring配置文件路径
	private static final String springPath = "conf/spring/applicationContext.xml";

	public static void main(String[] args) {
		int port;
		final int websocketPort;
		String errMsg = "请传入端口号 ";
		if (args.length != 2) {
			System.out.println(errMsg);
			return;
		}
		try {
			port = Integer.parseInt(args[0]);
			websocketPort = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println(errMsg);
			return;
		}
		context = new ClassPathXmlApplicationContext(springPath);

		RUserDao rUserDao = context.getBean(RUserDao.class);
		rUserDao.clearIpIds();
		// 设置log4j日志目录。jar包中只能使用/相对项目根路径查找文件
		PropertyConfigurator.configure(ServerInit.class.getResource("/" + log4jPath));
		logger.info("信息：starting server");
		ThreadPool.getExecutorService().execute(new WebsocketInit(websocketPort));
		ThreadPool.getExecutorService().execute(new ServerInit(port));
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				ThreadPool.shutdownNow();
			}
		});
	}
}