package com.sibu.chat.common.bean.vo.http;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Code;

public class HttpErrorResult {
	public static JSONObject getResult(Code code, String errMsg) {
		JSONObject result = new JSONObject();
		result.put("code", code.getCode());
		result.put("msg", errMsg);
		return result;
	}
}
