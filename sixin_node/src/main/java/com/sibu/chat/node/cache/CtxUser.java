package com.sibu.chat.node.cache;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;

import com.sibu.chat.common.bean.po.User;
import com.sibu.chat.node.main.Main;
import com.sibu.chat.node.redis.RUserDao;

/**
 * 保存tcp连接与用户的对应关系
 * @author caishiyu
 */
public class CtxUser {

	private static RUserDao ruserDao = Main.context.getBean(RUserDao.class);;
	/**
	 * 根据ChannelHandlerContext中获取user
	 */
	private static final AttributeKey<User> key = AttributeKey.valueOf("user");
	/**
	 * 根据用户id获取user和ctx
	 */
	private static final ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<Integer, User>();

	/**
	 * 添加新user
	 * @param user user类
	 * @param ctx tcp连接
	 */
	public static void addCache(User user) {
		user.getCtx().attr(key).set(user);
		users.put(user.getId(), user);
		// 存放到redis缓存
		ruserDao.addServerUser(user.getId());
	}

	/**
	 * 清除某个cache
	 * @param id 用户id
	 * @return 与用户对应的ctx
	 */
	public static ChannelHandlerContext clearCache(int id) {
		// 删除服务中的信息
		ruserDao.delServerUser(id);
		User user = users.get(id);
		ChannelHandlerContext ctx = user.getCtx();
		// 从ctx中清除对应的user
		ctx.attr(key).remove();
		// 从users中清除对应的user
		users.remove(id);
		user = null;
		return ctx;
	}

	/**
	 * 根据用户id获取user
	 * @param id 用户id
	 * @return user
	 */
	public static User getUserById(int id) {
		return users.get(id);
	}

	/**
	 * 根据用户id获取ctx
	 * @param id 用户id
	 * @return 用户ctx
	 */
	public static ChannelHandlerContext getCtxById(int id) {
		User user = users.get(id);
		return user == null ? null : user.getCtx();
	}

	/**
	 * 根据与tcp连接对应的user
	 * @param ctx tcp连接
	 * @return user
	 */
	public static User getUserByCtx(ChannelHandlerContext ctx) {
		Attribute<User> attr = ctx.attr(key);
		return attr == null ? null : attr.get();
	}

	/**
	 * 判断缓存中是否存在某个用户
	 * @param id 用户id
	 * @return 存在：true 不存在：false
	 */
	public static boolean hasUser(int id) {
		return users.containsKey(id);
	}

	/**
	 * 获取当前登录用户数量
	 * @return 用户数量
	 */
	public static int usersCount() {
		return users.size();
	}
}
