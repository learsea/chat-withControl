package com.sibu.chat.common.bean.po;

/**
 * 聊天记录po
 * @author caishiyu
 *
 */
public class Record {
	// 说话人id
	private Integer talkerId;
	// 接收人id
	private Integer listenerId;
	// 聊天时间
	private long time;
	// 消息体
	private String content;
	// 群id
	private Integer groupId;
	private String msgId;

	public Record() {
	}

	public Record(Integer talkerId, Integer listenerId, long time, String content, String msgId) {
		this.talkerId = talkerId;
		this.listenerId = listenerId;
		this.time = time;
		this.content = content;
		this.msgId = msgId;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getTalkerId() {
		return talkerId;
	}

	public void setTalkerId(Integer talkerId) {
		this.talkerId = talkerId;
	}

	public Integer getListenerId() {
		return listenerId;
	}

	public void setListenerId(Integer listenerId) {
		this.listenerId = listenerId;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
}
