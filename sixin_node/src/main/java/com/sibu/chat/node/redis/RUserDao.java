package com.sibu.chat.node.redis;

import io.netty.channel.ChannelHandlerContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.sibu.chat.node.GlobeConfig;
import com.sibu.chat.node.cache.CtxUser;

@Repository
public class RUserDao {
	@Autowired
	@Qualifier("ipIdRedisTemplate")
	private StringRedisTemplate redisTemplate;

	// 校验用户是否登录
	public boolean checkLogin(int uid) {
		ChannelHandlerContext listenerCtx = CtxUser.getCtxById(uid);
		if (listenerCtx == null) {
			// 对方不在本节点
			String userAddress = getUserAddress(uid);
			if (userAddress != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * 用户登录，设置redis中的id-address和host-ids
	 * @param uid 用户id
	 */
	public void addServerUser(int uid) {
		redisTemplate.boundValueOps("" + uid).set(GlobeConfig.HTTP_ADDRESS);
		redisTemplate.boundSetOps(GlobeConfig.SERVER_HOST).add("" + uid);
	}

	/**
	 * 用户注销，删除redis中的id-address和host-ids
	 * @param uid
	 */
	public void delServerUser(int uid) {
		redisTemplate.delete("" + uid);
		redisTemplate.boundSetOps(GlobeConfig.SERVER_HOST).remove("" + uid);
	}

	/**
	 * 根据用户id获取其对应的服务器hessian address
	 * @param uid 用户id
	 * @return hessian address
	 */
	public String getUserAddress(int uid) {
		return (String) redisTemplate.boundValueOps("" + uid).get();
	}
}
