package com.garow.tcpgateway.server.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * 服务上下文方便在多例中调用
 * 
 * @author seg
 *
 */
@Component
public class TcpGatewayServiceContext {
	@Resource
	private ConnectEventService			connectEventService;
	private static ConnectEventService	mConnectEventService;

	@PostConstruct
	void init() {
		mConnectEventService = connectEventService;
	}

	public static ConnectEventService getConnectEventService() {
		return mConnectEventService;
	}

}
