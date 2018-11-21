package com.garow.route.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * 路由服务上下文
 * @author seg
 *
 */
@Component
public class RouteServiceContext {
	@Resource
private InternalConnectService internalConnectService;
	private static InternalConnectService mInternalConnectService;
	@PostConstruct
	void init() {
		mInternalConnectService = internalConnectService;
	}
	public static InternalConnectService getInternalConnectService() {
		return mInternalConnectService;
	}
	
}
