package com.sibu.chat.control.redis;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.sibu.chat.control.bean.pojo.CtxServer;

@Repository
public class RUserDao {
	@Autowired
	@Qualifier("ipIdRedisTemplate")
	private StringRedisTemplate redisTemplate;

	public void clearIpIds() {
		redisTemplate.execute(new RedisCallback<String>() {
			public String doInRedis(RedisConnection connection) {
				connection.flushDb();
				return "ok";
			}
		});
	}

	public void delServer(CtxServer server) {
		String host = server.getHost();
		Set<String> set = redisTemplate.boundSetOps(host).members();
		// 删除host:ids映射
		redisTemplate.delete(host);
		// 删除id:ip映射
		redisTemplate.delete(set);
	}
}
