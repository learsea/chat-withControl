package com.sibu.chat.node.dao;

import com.sibu.chat.common.bean.po.User;
import com.sibu.chat.node.utils.datasource.DataSource;
import com.sibu.chat.node.utils.datasource.DataSourceKey;

/**
 * 用户持久层类。
 * @author caishiyu
 */
public interface UserDao {
	/**
	 * 根据用户id获取user
	 * @param id 用户id
	 * @return user
	 */
	public User getUserById(int id);

	/**
	 * 登录用户下线后保存用户信息，用来做消息推送
	 * @param user
	 * @return 保存条数
	 */
	public int saveUser(User user);

	/**
	 * 登录用户下线后保存用户信息，用来做消息推送
	 * @param user
	 * @return 保存条数
	 */
	@DataSource(DataSourceKey.special)
	public int saveUserLog(User user);

	/**
	 * 根据用户id删除user
	 * @param id 用户id
	 * @return 删除条数
	 */
	public int deleteUser(String id);

	/**
	 * 跟新登录用户
	 * @param user 用户
	 * @param count 设置badge
	 * @return 跟新条数
	 */
	public int updateUser(User user);

	public String getSinXinName(int tid);
}
