package com.sibu.chat.node.cache;

import java.util.concurrent.atomic.AtomicLong;

public class MsgNum {
	// 放入redis的key
	public static final String SIXIN_MSG_NUM_KEY = "sixinMsgNumKey";
	public static final AtomicLong SIXIN_MSG_NUM = new AtomicLong(0);
}
