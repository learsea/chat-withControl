package com.sibu.chat.control.utils;

import org.tempuri.WebService;
import org.tempuri.WebServiceSoap;

import com.sibu.chat.common.utils.Config;
import com.sibu.chat.control.bean.pojo.CtxServer;

public class SendMsgUtil {

	public static void sendMsg(CtxServer server, String msg) {
		String[] mobiles = Config.getString("error.mobile").split("/");
		for (String mobile : mobiles) {
			SendMsgUtil.sendMsg(mobile, server.getHost() + msg);
		}
	}

	private static String sendMsg(String mobile, String content) {

		WebServiceSoap soap = new WebService().getWebServiceSoap();

		return soap.sendSMS("SDK-BBX-010-20982", "7c7-e630 ", mobile, content + "【思埠】");

	}
}
