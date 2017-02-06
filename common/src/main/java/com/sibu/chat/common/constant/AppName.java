package com.sibu.chat.common.constant;

/**
 * APP 类型
 * @author pxw
 * 
 */
public enum AppName {
	appName(""),
	// 私信
	SiXin("SiXin"),
	// 另一个版本的私信
	NewSiXin("SiXin"),
	// 鲜花网
	Xianhua("Xianhua"),
	// 快购
	KuaiGou("KuaiGou"),
	// 快购桌面版
	KuaiGou_pc("KuaiGou"),
	// 快购游客（咨询客服）
	visitor("KuaiGou"),

	// 手思
	ShouQuan("ShouQuan"),
	// 手思后台
	Admin("ShouQuan"),
	// 授权桌面版
	ShouQuan_pc("ShouQuan");

	private AppName(String serverType) {
		this.serverType = serverType;
	}

	// 该节点所属的服务类型
	private String serverType;

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

}