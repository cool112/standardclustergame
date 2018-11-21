package com.garow.tcpgateway.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;

/**
 * 用户客户端连接管理
 * 
 * @author seg
 *
 */
public class ClientConnectManager {
	private static final Logger			LOG					= LoggerFactory.getLogger(ClientConnectManager.class);
	private static Map<String, Channel>	clientChannelMap	= new ConcurrentHashMap<String, Channel>();

	public static void addConnect(String sessionId, Channel ch) {
		Channel oldCh = clientChannelMap.putIfAbsent(sessionId, ch);
		if (oldCh != null && oldCh.isWritable()) {
			LOG.warn("repeat connect:" + sessionId);
			ch.close();
		}
	}

	public static Channel getChannel(String sessionId) {
		return clientChannelMap.get(sessionId);
	}
}
