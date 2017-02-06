package com.sibu.chat.node.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sibu.chat.common.bean.po.ChatGroup;
import com.sibu.chat.common.bean.po.GroupUser;

/**
 * 群组持久层类。
 * @author caishiyu
 */
@Repository
public interface GroupDao {

	/**
	 * 新建群
	 * @param chatGroup 群
	 * @return 新建条数
	 */
	public int newGroup(ChatGroup chatGroup);

	/**
	 * 解散群
	 * @param chatGroup 群
	 * @return 删除条数
	 */
	public int dropGroup(ChatGroup chatGroup);

	/**
	 * 修改群信息
	 * @param group 要修改的群
	 * @return
	 */
	public int alterGroup(ChatGroup group);

	/**
	 * 添加群用户
	 * @param groupUsers 群用户列表
	 * @return 添加条数
	 */
	public int addGroupUsers(List<GroupUser> groupUsers);

	/**
	 * 添加一个群用户
	 * @param groupUser 群用户
	 * @return 添加条数
	 */
	public int addGroupUser(GroupUser groupUser);

	/**
	 * 删除群用户
	 * @param groupUser 群用户
	 * @return 删除条数
	 */
	public int delGroupUser(GroupUser groupUser);

	/**
	 * 删除群所有用户
	 * @param groupId 群id
	 * @return 删除条数
	 */
	public int delGroupUsersByGroupId(int groupId);

	/**
	 * 获取群用户
	 * @param groupId 群id
	 * @return 群用户id
	 */
	public List<GroupUser> getGroupUsesByGroupId(int groupId);

	/**
	 * 获取群用户
	 * @param groupId 群id
	 * @return 群用户id
	 */
	public List<Integer> getGroupUsesIdsByGroupId(int groupId);

	/**
	 * 根据群id获取群
	 * @param groupId 群id
	 * @return 群
	 */
	public ChatGroup getChatGroupByGroupId(int groupId);

	/**
	 * 根据群id查询是否存在该群
	 * @param groupId 群id
	 * @return 存在返回true，否则返回false
	 */
	public int existGroup(int groupId);

	/**
	 * 根据用户id获取用户所有群
	 * @param id 用户id
	 * @return 
	 */
	public List<Map<String, Object>> getGroups(int uid);
}
