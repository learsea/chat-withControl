package com.sibu.chat.node.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.sibu.chat.node.cache.MsgNum;

@Repository
public class RMsgDao {
	@Autowired
	@Qualifier("redisTemplate")
	private StringRedisTemplate redisTemplate;

	// 增加一条消息数量
	public void addSixinMsgNum(long num) {
		redisTemplate.boundValueOps(MsgNum.SIXIN_MSG_NUM_KEY).increment(num);
	}
}
