package com.sibu.chat.node;

import com.sibu.chat.common.utils.Config;

public class GlobeConfig {

	public static final String SERVER_HOST = Config.getString("server.host"); // 本机做为socket
																				// server时
	public static final int SERVER_PORT = Config.getInt("server.port"); // 服务器端口
	public static final String serverType = Config.getString("server.node.serverType"); // 服务类型
	public static final int SERVER_LIMITCOUNT = Config.getInt("server.limitCount"); // 最大用户数,评估性能用的
	public static final String CENTER_HOST = Config.getString("center.host"); // 中控服务器的地址
	public static final int CENTER_PORT = Config.getInt("center.port"); // 中控服务器的端口
	public static final String CENTER_ADDRESS = CENTER_HOST + ":" + CENTER_PORT; // 中控服务器的地址
	public static final int HTTP_PORT = Config.getInt("http.port");
	public static final String HTTP_ADDRESS = SERVER_HOST + ":" + HTTP_PORT; // hessian地址

	public static final int WEBSOCKET_PORT = Config.getInt("websocket.port"); // hessian地址
}
