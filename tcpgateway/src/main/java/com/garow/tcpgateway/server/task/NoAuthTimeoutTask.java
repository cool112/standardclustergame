package com.garow.tcpgateway.server.task;

import java.util.concurrent.TimeUnit;

import com.garow.net.socket.constants.ContextKeys;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * 无权限超时
 * 
 * @author seg
 *
 */
public class NoAuthTimeoutTask implements Runnable {
	private ChannelHandlerContext ctx;

	public NoAuthTimeoutTask(ChannelHandlerContext ctx) {
		super();
		this.ctx = ctx;
	}

	@Override
	public void run() {
		if (ctx.channel().attr(ContextKeys.KEY_AUTH).get())
			return;
		long curTime = System.currentTimeMillis();
		long timeout = 10000;
		if (ctx.channel().attr(ContextKeys.KEY_LAST_LIVE).get() + timeout > curTime)
			ctx.executor().schedule(this, 10, TimeUnit.SECONDS);
		else
			ctx.close();
	}

}
