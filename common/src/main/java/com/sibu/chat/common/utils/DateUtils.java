package com.sibu.chat.common.utils;

import java.util.Date;

/**
 * 时间工具类。
 * @author caishiyu
 */
public class DateUtils {
	/**
	 * @return 当前毫秒时间戳
	 */
	public static long getTimeStamp() {
		return new Date().getTime();
	}
}
