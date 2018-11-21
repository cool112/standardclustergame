package com.garow.net.socket.server.config;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;
/**
 * proto配置上下文
 * @author seg
 *
 */
@Component
public class ProtoConfigContext {
	@Resource
	private ServerConfig serverConfig;
	private static ServerConfig mServerConfig;

	@PostConstruct
	void init() {
		mServerConfig = serverConfig;
	}

	public static ServerConfig getServerConfig() {
		return mServerConfig;
	}
}
