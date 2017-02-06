package com.sibu.chat.common.constant;

/**
 * 操作类型
 * @author pxw
 *
 */
public enum Operation {
	operation,
	// 客户端连接
	connect,
	// 客户端断开连接
	disconnect,
	// 登录
	login,
	// 获取离线消息
	getOfflineMsg,
	// 获取离线通知
	getOfflineNtc,
	// 注销
	logout,
	// 聊天
	chat,
	// 撤回已发送的单聊消息
	recall,
	// 撤回已发送的群消息
	recallG,
	// 新建群
	newGroup,
	// 修改群名称
	alterGroup,
	// 加入群
	joinGroup,
	// 退出群
	exitGroup,
	// 解散群
	dropGroup,
	// 群聊天
	groupChat,
	// 服务器通知
	notice,
	// 增加服务器节点
	addServer,
	// 选择服务器节点
	chooseServer,
	// 添加好友
	addFriend,
	// 删除好友
	delF,
	// 拉黑
	black,
	// 邀请进群
	inviteToG,
	// T用户
	kickout,
	// 用户同意加好友
	aggreF,
	// 减少推送气泡条数
	badge,
	// 是否需要推送
	needPush,
	// 获取群信息
	getGroups,
	// 根据群id查询群消息
	getGById,
	// 获取群用户
	getGUsers,
	// 未知操作
	unknown
}
