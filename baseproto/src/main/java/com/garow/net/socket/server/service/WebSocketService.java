package com.garow.net.socket.server.service;
/**
 * 
 * @author seg
 *
 */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WebSocketService {
	private static Map<String, WebSocketServerHandshaker> webSocketHandshakerMap = new ConcurrentHashMap<String, WebSocketServerHandshaker>();

	public static Map<String, WebSocketServerHandshaker> getWebSocketHandshakerMap() {
		return webSocketHandshakerMap;
	}
	
}
