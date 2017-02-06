package com.dbay.apns4j.impl;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import com.dbay.apns4j.IApnsService;
import com.dbay.apns4j.model.ApnsConfig;
import com.dbay.apns4j.model.Payload;

/**
 * @author RamosLi
 *
 */
public class Apns4jDemo {
	private static IApnsService apnsService;

	private static IApnsService getApnsService() {
		if (apnsService == null) {
			ApnsConfig config = new ApnsConfig();
			InputStream is = Apns4jDemo.class.getResourceAsStream("/conf/apns/ShouQuan.p12");
			config.setKeyStore(is);
			config.setDevEnv(true);
			config.setPassword("dev123456");
			config.setPoolSize(3);
			// 假如需要在同个java进程里给不同APP发送通知，那就需要设置为不同的name
			// config.setName("welove1");
			apnsService = ApnsServiceImpl.createInstance(config);
		}
		return apnsService;
	}

	public static void main(String[] args) throws InterruptedException {
		IApnsService service = getApnsService();
		while (true) {
			TimeUnit.SECONDS.sleep(1);
			// send notification
			String token = "86bb2ac0478cb8a7b031f3d772b6b0751a4276ad4fca37294ccab77651dbbc4b";

			Payload payload = new Payload();
			payload.setAlert("How are you?");
			// If this property is absent, the badge is not changed. To remove
			// the
			// badge, set the value of this property to 0
			payload.setBadge(100);
			// set sound null, the music won't be played
			// payload.setSound(null);
			payload.setSound("default");
			service.sendNotification(token, payload);
		}
	}
}
