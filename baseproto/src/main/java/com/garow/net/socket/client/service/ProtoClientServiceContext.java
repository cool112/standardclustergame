package com.garow.net.socket.client.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;
/**
 * 客户端服务上下文
 * @author seg
 *
 */
@Component
public class ProtoClientServiceContext {
	@Resource
	private ConnectService			connectService;
	private static ConnectService	mConnectService;

	@PostConstruct
	void init() {
		mConnectService = connectService;
	}

	public static ConnectService getConnectService() {
		return mConnectService;
	}

}
