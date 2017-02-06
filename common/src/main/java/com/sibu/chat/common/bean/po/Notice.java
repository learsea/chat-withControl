package com.sibu.chat.common.bean.po;

public class Notice {
	private String type;
	// 通知接收人id
	private Integer userId;
	// 操作人id
	private Integer operatorUid;
	// 被操作群id
	private Integer relatedGid;
	// 被操作人id串，多个id用,分隔
	private String relatedUids;
	// 操作时间
	private long createtime;
	// 消息id
	private String msgId;
	// 附加内容
	private String content;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public Integer getRelatedGid() {
		return relatedGid;
	}

	public void setRelatedGid(Integer relatedGid) {
		this.relatedGid = relatedGid;
	}

	public String getRelatedUids() {
		return relatedUids;
	}

	public void setRelatedUids(String relatedUids) {
		this.relatedUids = relatedUids;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getOperatorUid() {
		return operatorUid;
	}

	public void setOperatorUid(Integer operatorUid) {
		this.operatorUid = operatorUid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
