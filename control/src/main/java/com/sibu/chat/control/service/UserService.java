package com.sibu.chat.control.service;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.sibu.chat.control.bean.pojo.CtxServer;
import com.sibu.chat.control.cache.CtxServerCache;
import com.sibu.chat.control.utils.SendMsgUtil;

/**
 * 用户相关的Service
 * @author pxw
 *
 */
public class UserService {

	private static Logger logger = Logger.getLogger(UserService.class);

	/**
	 * 用户登陆时，记录到redis
	 * @param loginIV
	 * @return
	 */
	public static String connect(ChannelHandlerContext ctx) {
		CtxServer ctxServer = CtxServerCache.getCtxServerByCtx(ctx);
		ctxServer.incUserCount();
		if (ctxServer.getUserCount() >= ctxServer.getLimitCount() && !ctxServer.isSendMsg()) {
			logger.info("节点" + ctxServer.getHost() + "负载已满" + ctxServer.getLimitCount());
			// 该节点负载已满，发送短信通知
			SendMsgUtil.sendMsg(ctxServer, "负载已满" + ctxServer.getLimitCount());
			ctxServer.setSendMsg(true);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(ctxServer.getHost() + ":" + ctxServer.getUserCount() + "/" + ctxServer.getLimitCount());
		}
		return null; // 返回空，不需要回写信息给node节点
	}

	/**
	 * 用户退出时，清除redis
	 * @param logoutIV
	 * @return
	 */
	public static String disconnect(ChannelHandlerContext ctx) {
		CtxServer ctxServer = CtxServerCache.getCtxServerByCtx(ctx);
		ctxServer.decUserCount();
		if (logger.isDebugEnabled()) {
			logger.debug(ctxServer.getHost() + ":" + ctxServer.getUserCount() + "/" + ctxServer.getLimitCount());
		}
		return null; // 返回空，不需要回写信息给node节点
	}
}
