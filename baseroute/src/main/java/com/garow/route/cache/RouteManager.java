package com.garow.route.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由管理器
 * 
 * @author seg
 *
 */
public class RouteManager {
	/** sessionRouteMap.sessionId.app=host */
	private static Map<String, Map<String, String>> sessionRouteMap = new ConcurrentHashMap<String, Map<String, String>>();

	/**
	 * 改变应用路由
	 * 
	 * @param sessionId
	 * @param app
	 * @param host
	 */
	public static void changeRoute(String sessionId, String app, String host) {
		Map<String, String> routeMap = sessionRouteMap.get(sessionId);
		if (routeMap == null) {
			routeMap = new HashMap<String, String>();
			Map<String, String> old = sessionRouteMap.putIfAbsent(sessionId, routeMap);
			if (old != null)
				routeMap = old;
		}
		routeMap.put(app, host);
	}

	/**
	 * 获取应用路由
	 * 
	 * @param sessionId
	 * @param app
	 * @return
	 */
	public static String getRoute(String sessionId, String app) {
		Map<String, String> routeMap = sessionRouteMap.get(sessionId);
		if (routeMap == null)
			return null;
		return routeMap.get(app);
	}

	/**
	 * 改变session,比如断线重连
	 * 
	 * @param oldSession
	 * @param newSession
	 */
	public static void changeSession(String oldSession, String newSession) {
		Map<String, String> routeMap = sessionRouteMap.remove(oldSession);
		if (routeMap == null)
			return;

		sessionRouteMap.put(newSession, routeMap);
	}
}
