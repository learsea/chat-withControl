package com.sibu.chat.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.exception.BusinessException;

public class JSONUtils {
	public static String parse(Object o) {
		return JSONObject.toJSONString(o) + "\0";
	}

	public static JSONArray getJSONArray(JSONObject in, String param) {
		JSONArray array = null;
		try {
			array = in.getJSONArray(param);
			if (array == null) {
				throw new BusinessException("参数错误," + param);
			}
			return array;
		} catch (Exception e) {
			throw new BusinessException("参数错误," + param);
		}
	}

	public static int getInt(JSONObject in, String param) {
		try {
			int i = in.getIntValue(param);
			return i;
		} catch (Exception e) {
			throw new BusinessException("参数错误," + param);
		}
	}
}
