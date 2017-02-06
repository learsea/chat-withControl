package com.sibu.chat.control.service;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.bean.vo.tcp.TcpErrorResult;
import com.sibu.chat.common.bean.vo.tcp.TcpSuccessResult;
import com.sibu.chat.common.constant.AppName;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.exception.BusinessException;
import com.sibu.chat.common.utils.JSONUtils;
import com.sibu.chat.control.bean.pojo.CtxServer;
import com.sibu.chat.control.cache.CtxServerCache;

public class ServerService {

	private static Logger logger = Logger.getLogger(ServerService.class);

	/**
	 * 服务器加入组群
	 * @param iv 入参
	 * @return 返回消息
	 */
	public static String addServer(JSONObject in, ChannelHandlerContext ctx) {
		CtxServer ctxServer = new CtxServer();
		ctxServer.setHost(in.getString("host"));
		ctxServer.setPort(JSONUtils.getInt(in, "port"));
		ctxServer.setWebsocketPort(JSONUtils.getInt(in, "websocketPort"));
		ctxServer.setLimitCount(JSONUtils.getInt(in, "limitCount"));
		AppName app = null;
		try {
			app = AppName.valueOf(in.getString("appName"));
		} catch (Exception e) {
			return JSONUtils.parse(TcpErrorResult.getResult(Operation.chooseServer, "appName错误"));
		}
		// 设置节点服务类型
		ctxServer.setServerType(app.getServerType());
		ctxServer.setCtx(ctx);
		int count = CtxServerCache.addServer(ctxServer);
		logger.info(ctxServer.toString() + "加入集群,当前节点数为" + count);

		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(Operation.addServer);
		return JSONUtils.parse(result);
	}

	/**
	 * 客户端选择聊天服务器
	 * @param iv 入参
	 * @return 返回消息
	 */
	public static String chooseServer(String appName, ChannelHandlerContext ctx) {
		try {
			AppName app = null;
			try {
				app = AppName.valueOf(appName);
			} catch (Exception e) {
				return JSONUtils.parse(TcpErrorResult.getResult(Operation.chooseServer, "appName错误"));
			}
			// 根据该app所属的服务类型选择服务器
			CtxServer server = CtxServerCache.getServer(app.getServerType());

			// 拼接返回结果
			JSONObject result = TcpSuccessResult.getResult(Operation.chooseServer);
			result.put("host", server.getHost());
			result.put("port", server.getPort());
			result.put("websocketPort", server.getWebsocketPort());
			ctx.writeAndFlush(JSONUtils.parse(result)).addListener(ChannelFutureListener.CLOSE);
			return null;
		} catch (BusinessException e) {
			return JSONUtils.parse(TcpErrorResult.getResult(Operation.chooseServer, e.getMessage()));
		}
	}
}
