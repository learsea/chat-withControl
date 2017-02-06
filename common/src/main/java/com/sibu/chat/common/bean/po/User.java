package com.sibu.chat.common.bean.po;

import io.netty.channel.ChannelHandlerContext;

/**
 * 用户类
 * @author caishiyu
 */
public class User implements Cloneable {
	private int autoId;
	// 用户id
	private Integer id;
	// 姓名
	private String name;
	// 客户端类型
	private String clientType;
	// 手机唯一码
	private String deviceToken;
	// 用户登录项目名
	private String appName;
	// 登录时间
	private Long loginTime;
	private long createtime;

	// 是否需要推送
	private Boolean needPush;

	// 推送气泡消息条数
	private Integer badge;

	// 是否是开发模式
	private Boolean dev;

	// 用户对应的tcp连接
	private ChannelHandlerContext ctx;

	public User() {
	}

	@Override
	public User clone() {
		User user = null;
		try {
			user = (User) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return user;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Long loginTime) {
		this.loginTime = loginTime;
	}

	public Boolean getNeedPush() {
		return needPush;
	}

	public void setNeedPush(Boolean needPush) {
		this.needPush = needPush;
	}

	public Integer getBadge() {
		return badge;
	}

	public void setBadge(Integer badge) {
		this.badge = badge;
	}

	public Boolean getDev() {
		return dev;
	}

	public void setDev(Boolean dev) {
		this.dev = dev;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public int getAutoId() {
		return autoId;
	}

	public void setAutoId(int autoId) {
		this.autoId = autoId;
	}

}
