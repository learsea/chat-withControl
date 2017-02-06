package com.sibu.chat.common.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * flash跨域工具
 * @author pxw
 *
 */
public class FlashCrossUtils {
	public static final String FLASH_RESPONSE = getFlashResponse();
	public static final String FLASHREQUEST = "<policy-file-request/>";

	public static String getFlashResponse() {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(FlashCrossUtils.class.getResourceAsStream("/crossdomain.xml"));
			return document.asXML();
		} catch (DocumentException e) {
			e.printStackTrace();
			return "getFlashResponse ERROR";
		}
	}
}
