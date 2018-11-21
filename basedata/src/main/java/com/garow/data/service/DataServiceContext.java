package com.garow.data.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * 数据服务上下文
 * @author seg
 *
 */
@Component
public class DataServiceContext {
	@Resource
	private MessageSvc			messageSvc;
	private static MessageSvc	mMessageSvc;

	@PostConstruct
	void init() {
		mMessageSvc = messageSvc;
	}

	public static MessageSvc getMessageSvc() {
		return mMessageSvc;
	}

}
