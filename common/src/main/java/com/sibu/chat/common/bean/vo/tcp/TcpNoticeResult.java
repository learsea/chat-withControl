package com.sibu.chat.common.bean.vo.tcp;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.bean.po.Notice;
import com.sibu.chat.common.bean.po.Record;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.constant.Type;

public class TcpNoticeResult {
	public static enum Msg {
		timeout("timeout"), anotherUser("another user");
		private String msg;

		private Msg(String msg) {
			this.msg = msg;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

	}

	/**
	 * 操作通知
	 */
	public static JSONObject getResult(Notice notice) {
		JSONObject ntc = new JSONObject();
		ntc.put(Operation.operation.toString(), Operation.notice);
		ntc.put(Type.type.toString(), notice.getType());
		ntc.put("detail", notice);
		return ntc;
	}

	/**
	 * 下线通知
	 * @param msg 下线类型
	 */
	public static JSONObject getResult(Msg msg) {
		JSONObject ntc = new JSONObject();
		ntc.put(Operation.operation.toString(), Operation.notice);
		ntc.put(Type.type.toString(), Type.disconnect);
		ntc.put("msg", msg.getMsg());
		return ntc;
	}

	/**
	 * 聊天通知
	 * @param type 区分单聊还是群聊
	 */
	public static JSONObject getResult(Record record, Type type) {
		JSONObject ntc = new JSONObject();
		ntc.put(Operation.operation.toString(), Operation.notice);
		ntc.put(Type.type.toString(), type);
		ntc.put("record", record);
		return ntc;
	}
}
