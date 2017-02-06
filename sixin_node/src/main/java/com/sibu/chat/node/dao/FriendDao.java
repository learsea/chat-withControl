package com.sibu.chat.node.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendDao {

	public int addFriend(@Param("uid") int uid, @Param("fid") int fid);

	public int delFriend(@Param("uid") int uid, @Param("fid") int fid);

	public List<Integer> getFriendsAsc(@Param("id") int id);

	public List<Integer> getFriendsDesc(@Param("id") int id);

	public int isFriend(@Param("id") int id, @Param("fid") int fid);

	public String getRemark(@Param("tid") int tid, @Param("lid") int lid);

	public String getBids(int id);

	/**
	 * 跟新黑名单
	 */
	public int updateBlacklist(@Param("id") int id, @Param("bids") String bids);
}
