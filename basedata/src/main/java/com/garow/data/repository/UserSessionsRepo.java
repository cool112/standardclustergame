package com.garow.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.garow.data.model.UserSession;
/**
 * 用户与session关系集合
 * @author seg
 *
 */
public interface UserSessionsRepo extends MongoRepository<UserSession, String> {
	UserSession findByUserId(String userId);
}
