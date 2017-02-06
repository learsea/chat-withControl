package com.sibu.chat.node.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.sibu.chat.common.bean.po.Record;
import com.sibu.chat.node.utils.datasource.DataSource;
import com.sibu.chat.node.utils.datasource.DataSourceKey;

/**
 * 聊天持久层类
 * @author caishiyu
 */
@Repository
public interface ChatDao {

	/**
	 * 根据用户id获取未读单聊记录
	 * @param id 用户id
	 * @param limit 查询条数
	 * @return 所有未读聊天记录	
	 */
	public List<Record> getLimitOne2OneRecordsByUserId(@Param("id") int id, @Param("limit") int limit);

	/**
	 * 根据用户id获取未读群聊记录
	 * @param id 用户id
	 * @param limit 查询条数
	 * @return 所有未读聊天记录
	 */
	public List<Record> getLimitGroupRecordsByUserId(@Param("id") int id, @Param("limit") int limit);

	/**
	 * 根据用户id删除单聊记录
	 * @param id 用户id
	 * @param limit 按时间排序，删除limit条
	 * @return 删除条数
	 */
	public int delLimitOne2OneRecordsByUserId(@Param("id") int id, @Param("limit") int limit);

	/**
	 * 根据用户id删除群聊记录
	 * @param id 用户id
	 * @param limit 按时间排序，删除limit条
	 * @return 删除条数
	 */
	public int delLimitGroupRecordsByUserId(@Param("id") int id, @Param("limit") int limit);

	/**
	 * 根据用户id删除所有单聊记录
	 * @param id 用户id
	 * @return 删除条数
	 */
	public int delAllOne2OneRecordsByUserId(@Param("id") String id);

	/**
	 * 根据用户id删除所有群聊记录
	 * @param id 用户id
	 * @return 删除条数
	 */
	public int delAllGroupRecordsByUserId(@Param("id") int id);

	/**
	 * 获取单聊记录条数
	 * @param id 用户id
	 * @return 条数
	 */
	public int getOne2OneRecordsCountByUserId(int id);

	/**
	 * 获取群聊记录条数
	 * @param id 用户id
	 * @return 条数
	 */
	public int getGroupRecordsCountByUserId(int id);

	/**
	 * 保存聊天记录
	 * @param record 聊天记录bean
	 * @return 保存条数
	 */
	public int saveRecord(Record record);

	/**
	 * 根据群id删除所有聊天记录
	 * @param groupId 群id
	 * @return 删除条数
	 */
	public int delAllGroupRecordsByGroupId(int groupId);

	/**
	 * 插入聊天记录 不删除
	 * @param record
	 */
	@DataSource(DataSourceKey.special)
	public void saveRecordLog(Record record);

	/**
	 * 撤销单聊消息
	 * @param loginId 说话人id
	 * @param listenerId 接收人id
	 * @param msgId 消息id
	 */
	public int recall(@Param("tid") int loginId, @Param("lid") int listenerId, @Param("mid") String msgId);

	/**
	 * 撤销群聊消息
	 * @param loginId 说话人id
	 * @param gid 群id
	 * @param msgId 消息id
	 */
	public int recallG(@Param("tid") int loginId, @Param("gid") int gid, @Param("mid") String msgId);

}
