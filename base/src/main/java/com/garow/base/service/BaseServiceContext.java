package com.garow.base.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/**
 * 基础服务上下文
 * @author seg
 *
 */
@Component
public class BaseServiceContext implements ApplicationContextAware{
	@Resource
	private HttpService			httpService;
	private static HttpService	mHttpService;
	@Resource
	private ConfigClientService			configClientService;
	private static ConfigClientService	mConfigClientService;
	
	private static ApplicationContext mApplicationContext;

	@PostConstruct
	void init() {
		mHttpService = httpService;
		mConfigClientService = configClientService;
	}

	public static HttpService getHttpService() {
		return mHttpService;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		mApplicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return mApplicationContext;
	}

	public static ConfigClientService getConfigClientService() {
		return mConfigClientService;
	}
	
	
	
	

}
