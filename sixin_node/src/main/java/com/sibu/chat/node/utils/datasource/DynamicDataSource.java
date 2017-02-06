package com.sibu.chat.node.utils.datasource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 根据dao中的datasource注解动态选择数据源
 * 与spring的事务不兼容。如果使用transaction，则只会在同一个transaction方法中第一次执行dao时选择一次数据源，以后不再选择。
 * 即使事务属性选择NOT_SUPPORTED也一样。
 * 
 * @author caishiyu
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	// 保证线程安全
	private static final ThreadLocal<String> holder = new ThreadLocal<String>();
	private static Logger logger = Logger.getLogger(DynamicDataSource.class);

	public static void putDataSource(String name) {
		holder.set(name);
	}

	public static String getDataSouce() {
		String key = holder.get();
		holder.remove();
		if (logger.isDebugEnabled()) {
			logger.debug("using datasource：" + (key == null ? DataSourceKey.common : key));
		}
		return key;
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return getDataSouce();
	}

}
