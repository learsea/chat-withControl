package com.sibu.chat.common.bean.vo.tcp;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.constant.State;

public class TcpSuccessResult {
	public static JSONObject getResult(Operation operation) {
		JSONObject result = new JSONObject();
		result.put(Operation.operation.toString(), operation);
		result.put(State.state.toString(), State.success);
		return result;
	}
}
