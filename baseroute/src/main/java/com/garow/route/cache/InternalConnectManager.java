package com.garow.route.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;

/**
 * 服务器内部连接管理器,一个主机单链接足够了
 * 
 * @author seg
 *
 */
public class InternalConnectManager {
	private static final Logger			LOG			= LoggerFactory.getLogger(InternalConnectManager.class);
	private static Map<String, Channel>	channelMap	= new ConcurrentHashMap<String, Channel>();

	public static void addConnect(String host, Channel ch) {
		Channel oldCh = channelMap.putIfAbsent(host, ch);
		if (oldCh != null && oldCh.isWritable()) {
			LOG.warn("repeat connect:" + host);
			ch.close();
		}
	}

	public static Channel getChannel(String host) {
		return channelMap.get(host);
	}
}
