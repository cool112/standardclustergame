package com.garow.gameserver.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;
/**
 * 游戏服务上下文
 * @author seg
 *
 */
@Component
public class GameServiceContext {

	@Resource
	private DataInitService dataInitService;
	private static DataInitService mDataInitService;

	@PostConstruct
	void init() {
		mDataInitService = dataInitService;
	}

	public static DataInitService getDataInitService() {
		return mDataInitService;
	}

	
}
