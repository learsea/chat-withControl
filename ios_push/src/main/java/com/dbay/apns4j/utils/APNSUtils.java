package com.dbay.apns4j.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dbay.apns4j.IApnsService;
import com.dbay.apns4j.impl.ApnsServiceImpl;
import com.dbay.apns4j.model.ApnsConfig;
import com.dbay.apns4j.model.Payload;

/**
 * 读取并解析aons相关xml工具类。
 * @author caishiyu
 */
public class APNSUtils {

	public static void main(String[] args) throws InterruptedException {
		initAPNS();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", ApnsType.chat);
		params.put("uid", "324");
		while (true) {
			noticeByAppName("KuaiGou", true, "7bf351b30f00d388c2daf6d07893f82ce2a365c4e61f7598c33457445656999c",
					"是大家分开了江苏两地警方扣留撒娇的开工率计算的风格类似的公开垃圾啊圣诞快乐房管局卡拉三季度房管局阿斯利康对方国家森林空间广阔就算了空间塑料袋看风景离开家累块积苏开绿灯飞机", 102,
					params);
			TimeUnit.SECONDS.sleep(10);
		}
	}

	public static void initAPNS() {
		for (HashMap<String, String> apnsMap : getApns()) {
			ApnsConfig config = new ApnsConfig();
			String name = apnsMap.get("name");
			InputStream is = APNSUtils.class.getResourceAsStream("/conf/apns/" + apnsMap.get("path"));
			config.setKeyStore(is);
			config.setName(name);
			config.setPassword(apnsMap.get("password"));
			config.setDevEnv(Boolean.valueOf(apnsMap.get("isDevEnv")));
			ApnsServiceImpl.createInstance(config);
		}
	}

	// 推送类型
	public static enum ApnsType {
		chat
	}

	/**
	 * 
	 * @param appName 项目名
	 * @param isDev 是否开发模式
	 * @param token 手机token
	 * @param content 内容
	 * @param type 类型
	 * @param badge 推送消息气泡
	 */
	public static void noticeByAppName(String appName, boolean isDev, String token, String content, int badge,
			Map<String, Object> params) {
		appName += isDev ? "Dev" : "Pro";
		IApnsService service = ApnsServiceImpl.getCachedService(appName);
		Payload payload = new Payload();
		payload.setAlert(content);
		// 客户端图标显示的小数字
		payload.setBadge(badge + 1);
		// set sound null, the music won't be played
		//payload.setSound("default");
		payload.setSound("msgsound.mp3");
		payload.setParams(params);
		payload.setCategory("chatButton");
		// 保证推送内容最大256字节
		for (int i = 1; payload.toString().getBytes().length > 256; i++) {
			payload.setAlert(content.substring(0, content.length() - i));
		}
		service.sendNotification(token, payload);
	}

	public static List<HashMap<String, String>> getApns() {
		Element root = null;
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(APNSUtils.class.getResource("/conf/apns/apns.xml"));
			root = document.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		List<HashMap<String, String>> apnsList = new ArrayList<HashMap<String, String>>();
		// 获取所有子元素
		@SuppressWarnings("unchecked")
		List<Element> apnss = root.elements("apns");
		for (Element e : apnss) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", e.element("name").getText());
			map.put("password", e.element("password").getText());
			map.put("path", e.element("path").getText());
			map.put("isDevEnv", e.element("isDevEnv").getText());
			apnsList.add(map);
		}
		return apnsList;
	}
}