package com.sibu.chat.common.bean.po;

/**
 * 群组po。
 * @author caishiyu
 */
public class ChatGroup {
	// 群id
	private Integer groupId;
	// 群名称
	private String groupName;
	// 群头像
	private String icon;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

}
