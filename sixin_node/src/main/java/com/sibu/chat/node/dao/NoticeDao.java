package com.sibu.chat.node.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sibu.chat.common.bean.po.Notice;

@Repository
public interface NoticeDao {

	/**
	 * 保存通知消息
	 * @param notice 通知消息类
	 * @return 保存条数
	 */
	int save(Notice notice);

	/**
	 * 根据用户id获取通知消息
	 * @param userId 用户id
	 * @return 通知消息列表
	 */
	List<Notice> getNoticesByUserId(int userId);

	/**
	 * 根据用户id删除通知消息
	 * @param id 通知消息列表
	 */
	void delNoticeByUserId(int id);

}
