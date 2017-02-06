package com.sibu.chat.node.listener;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.sibu.chat.common.utils.ThreadPool;
import com.sibu.chat.node.cache.MsgNum;
import com.sibu.chat.node.redis.RMsgDao;

@Component
public class StatisticsListener implements ApplicationListener<ContextRefreshedEvent> {
	@Autowired
	private RMsgDao rMsgDao;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		setMsgNum();
	}

	// 每5秒设置一次消息数
	private void setMsgNum() {
		ThreadPool.getScheduledExecutorService().scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				// 设置完成后清零
				rMsgDao.addSixinMsgNum(MsgNum.SIXIN_MSG_NUM.getAndSet(0));
			}
		}, 5, 5, TimeUnit.SECONDS);
	}
}
