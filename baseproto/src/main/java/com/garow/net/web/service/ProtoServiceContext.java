package com.garow.net.web.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;
/**
 * proto公共服务上下文
 * @author seg
 *
 */
@Component
public class ProtoServiceContext {
	@Resource
	private InstanceService			instanceService;
	private static InstanceService	mInstanceService;

	@PostConstruct
	void init() {
		mInstanceService = instanceService;
	}

	public static InstanceService getInstanceService() {
		return mInstanceService;
	}
	
	
}
