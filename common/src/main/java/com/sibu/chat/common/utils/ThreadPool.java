package com.sibu.chat.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程池
 * @author pxw
 *
 */
public class ThreadPool {

	private static ExecutorService executorService = Executors.newFixedThreadPool(5);
	private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

	public static void shutdownNow() {
		executorService.shutdownNow();
		scheduledExecutorService.shutdownNow();
	}

	public static ExecutorService getExecutorService() {
		return executorService;
	}

	public static ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutorService;
	}
}
