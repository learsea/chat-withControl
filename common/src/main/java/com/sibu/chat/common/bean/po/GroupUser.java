package com.sibu.chat.common.bean.po;

/**
 * 群用户po
 * @author caishiyu
 */
public class GroupUser {
	// 群id
	private Integer groupId;
	// 用户id
	private Integer userId;
	// 是否是管理员 0：普通用户 1：管理员
	private int admin;

	public GroupUser() {
	}

	public GroupUser(Integer groupId, int userId) {
		this.groupId = groupId;
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public int getAdmin() {
		return admin;
	}

	public void setAdmin(int admin) {
		this.admin = admin;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

}
