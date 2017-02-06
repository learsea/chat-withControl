package com.sibu.chat.common.constant;

/**
 * 通知类型
 * @author pxw
 *
 */
public enum Type {
	type,
	// 单聊通知
	chat,
	// 撤销消息
	recall,
	// 群聊通知
	groupChat,
	// 下线
	disconnect,
	// 添加好友通知
	addFriend,
	// 邀请入群通知
	inviteToG,
	// 修改群通知
	alterG,
	// 新用户进入群通知
	newUserG,
	// 退出群通知
	exitG,
	// 被T出群通知
	beTG,
	// 解散群通知
	dropG,
	// 同意加好友
	aggreF,
	// 不同意加好友
	disAggreF,
	// 删除好友
	delF,
	// 拉黑
	black,

}
