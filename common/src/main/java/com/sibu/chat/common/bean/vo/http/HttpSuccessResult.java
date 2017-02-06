package com.sibu.chat.common.bean.vo.http;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Code;

public class HttpSuccessResult {
	public static JSONObject getResult() {
		JSONObject result = new JSONObject();
		result.put("msg", "ok");
		result.put("code", Code._100.getCode());
		return result;
	}
}
