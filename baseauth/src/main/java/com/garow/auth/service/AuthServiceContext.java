package com.garow.auth.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.data.mongo.MongoOperationsSessionRepository;
import org.springframework.stereotype.Component;

/**
 * 鉴权服务上下文
 * @author seg
 *
 */
@Component
public class AuthServiceContext {
	@Autowired
	private MongoOperationsSessionRepository mongoOperationsSessionRepository;
	/**mongo session 仓库，无缓存*/
	private static MongoOperationsSessionRepository mMongoOperationsSessionRepository;
	@PostConstruct
	void init() {
		mMongoOperationsSessionRepository = mongoOperationsSessionRepository;
	}
	
	public static MongoOperationsSessionRepository getMongoOperationsSessionRepository() {
		return mMongoOperationsSessionRepository;
	}
}
