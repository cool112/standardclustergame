package com.garow.net.socket.client;

import java.io.IOException;

import com.garow.net.socket.client.config.ClientConfig;

import io.netty.channel.ChannelFuture;

public interface NioClientPool {
	/**
	 * 连接同步
	 * @param config
	 * @return
	 * @throws IOException
	 */
	public ChannelFuture connect(final ClientConfig config) throws IOException;
	/**
	 * 连接
	 * @param config
	 * @param isSync
	 * @return
	 * @throws IOException
	 */
	public ChannelFuture connect(final ClientConfig config, boolean isSync) throws IOException;
	/**
	 * 获取目标连接
	 * @param tcpHost
	 * @return
	 */
	public ChannelFuture getChannelFuture(String tcpHost);
}
