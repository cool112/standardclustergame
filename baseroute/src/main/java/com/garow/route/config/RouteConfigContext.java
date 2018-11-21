package com.garow.route.config;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * 路由配置上下文
 * 
 * @author seg
 *
 */
@Component
public class RouteConfigContext {
	@Resource
	private RouteConfig			routeConfig;
	private static RouteConfig	mRouteConfig;

	@PostConstruct
	void init() {
		mRouteConfig = routeConfig;
	}

	public static RouteConfig getRouteConfig() {
		return mRouteConfig;
	}

}
