package com.sibu.chat.common.bean.vo.tcp;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.constant.State;

public class TcpErrorResult {
	public static JSONObject getResult(Operation op, String errMsg) {
		JSONObject result = new JSONObject();
		result.put(Operation.operation.toString(), op);
		result.put(State.state.toString(), State.fail);
		result.put("errMsg", errMsg);
		return result;
	}
}
