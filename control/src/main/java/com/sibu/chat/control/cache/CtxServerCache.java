package com.sibu.chat.control.cache;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.CollectionUtils;

import com.sibu.chat.common.exception.BusinessException;
import com.sibu.chat.control.bean.pojo.CtxServer;
import com.sibu.chat.control.main.Main;
import com.sibu.chat.control.redis.RUserDao;

/**
 * 
 * 该类有线程安全问题，为了性能先不考虑
 *
 */
public class CtxServerCache {
	private static AtomicInteger serverCount = new AtomicInteger(0);
	// 键为节点的服务类型，值为节点列表
	private static final Map<String, List<CtxServer>> serverMap = new ConcurrentHashMap<String, List<CtxServer>>(); // String是address
	private static RUserDao ruserDao = null;
	static {
		ruserDao = Main.context.getBean(RUserDao.class);
	}

	/**
	 * 添加新server
	 * @param CtxServer CtxServer类
	 */
	public static int addServer(CtxServer server) {
		if (serverMap.containsKey(server.getServerType())) {
			// 如果已存在该服务类型的结群，则将该节点放入集群中
			serverMap.get(server.getServerType()).add(server);
		} else {
			// 如果不存在，则新建一个集群
			List<CtxServer> appServerList = new ArrayList<CtxServer>();
			appServerList.add(server);
			serverMap.put(server.getServerType(), appServerList);
		}
		return serverCount.addAndGet(1);
	}

	/**
	 * 选举一个Server
	 */
	public static CtxServer getServer(String serverType) {
		List<CtxServer> serverList = serverMap.get(serverType);
		if (!CollectionUtils.isEmpty(serverList)) { // 如果该app有server则从中寻找
			Collections.sort(serverList); // 按照性能指标排序(性能好的排在最前)
			CtxServer topServer = serverList.get(0);
			if (topServer.getUserCount() >= topServer.getLimitCount()) { // 性能到达极限，从全部应用群中找最优
				return searchFromAll();
			} else {
				return topServer;
			}

		} else {
			// 如果该app没有server则任意挑选一个,这种情况比较不推荐,理论上应该每个app最少有一台socket服务器,否则比较消耗性能
			return searchFromAll();
		}

	}

	/**
	 * 从所有群集里面找
	 * @return
	 */
	private static CtxServer searchFromAll() {
		Collection<List<CtxServer>> allServers = serverMap.values();

		if (!CollectionUtils.isEmpty(allServers)) {
			List<CtxServer> serverForSort = new ArrayList<CtxServer>();
			for (List<CtxServer> servers : allServers) {
				serverForSort.addAll(servers);
			}
			Collections.sort(serverForSort);
			return serverForSort.get(0);
		}
		throw new BusinessException("没有可选的socket服务器");
	}

	/**
	 * 删除服务，一般移除服务时使用
	 * @param server
	 */
	public static int delServer(CtxServer server) {
		serverMap.get(server.getServerType()).remove(server);
		ruserDao.delServer(server);
		return serverCount.addAndGet(-1);
	}

	/**
	 * 根据ctx获取CtxServer
	 * @param appName
	 * @return
	 */
	public static CtxServer getCtxServerByCtx(ChannelHandlerContext ctx) {

		Collection<List<CtxServer>> allServers = serverMap.values();

		for (List<CtxServer> serverList : allServers) {
			for (int i = 0; i < serverList.size(); i++) {
				CtxServer server = serverList.get(i);
				if (ctx == server.getCtx()) {
					return server;
				}
			}
		}
		return null;
	}
}
