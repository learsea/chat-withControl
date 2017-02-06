package com.sibu.chat.node.utils.security;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class CheckCtx implements Delayed {
	private long trigger;
	private ChannelHandlerContext ctx;

	public CheckCtx(int delaySecond) {
		this.trigger = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delaySecond, TimeUnit.SECONDS);
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public int compareTo(Delayed o) {
		CheckCtx that = (CheckCtx) o;
		if (trigger > that.trigger) {
			return 1;
		}
		if (trigger < that.trigger) {
			return -1;
		}
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(trigger - System.nanoTime(), TimeUnit.NANOSECONDS);
	}
}