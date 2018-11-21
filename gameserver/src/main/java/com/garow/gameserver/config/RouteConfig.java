package com.garow.gameserver.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 路由配置，涉及到分配路由的功能，但该服并不需要长连接功能
 * @author seg
 *
 */
@Configuration
@ConfigurationProperties("route")
public class RouteConfig {
	/**需要路由的应用*/
	private List<String> apps;

	public List<String> getApps() {
		return apps;
	}

	public void setApps(List<String> apps) {
		this.apps = apps;
	}
	

}
