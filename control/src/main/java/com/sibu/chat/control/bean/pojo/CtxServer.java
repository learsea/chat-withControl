package com.sibu.chat.control.bean.pojo;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicInteger;

public class CtxServer implements Comparable<CtxServer> {

	private String host;
	private int port;
	private int websocketPort;
	// 节点的服务类型
	private String serverType;
	private ChannelHandlerContext ctx;
	private AtomicInteger userCount = new AtomicInteger(0);
	private int limitCount;
	// 是否已经发送过满负载通知
	private boolean sendMsg = false;

	public boolean isSendMsg() {
		return sendMsg;
	}

	public void setSendMsg(boolean sendMsg) {
		this.sendMsg = sendMsg;
	}

	public int getWebsocketPort() {
		return websocketPort;
	}

	public void setWebsocketPort(int websocketPort) {
		this.websocketPort = websocketPort;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public int getUserCount() {
		return userCount.get();
	}

	public void incUserCount() {
		userCount.incrementAndGet();
	}

	public void decUserCount() {
		userCount.decrementAndGet();
	}

	public int getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(int limitCount) {
		this.limitCount = limitCount;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	/**
	 * 获取性能，数值越大代表该服务器越空闲
	 * @return
	 */
	/*
	 */
	@Override
	public int compareTo(CtxServer objServer) {
		// 这么做是为了使性能越好的通过sort排越前
		if (limitCount - this.userCount.get() < objServer.getLimitCount() - objServer.userCount.get()) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return "CtxServer [serverType=" + serverType + ", host=" + getHost() + ", port=" + getPort()
				+ ", websocketPort=" + getWebsocketPort() + ", userCount=" + userCount + ", limitCount=" + limitCount
				+ "]";
	}
}
