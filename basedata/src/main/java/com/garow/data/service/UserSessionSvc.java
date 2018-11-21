package com.garow.data.service;

import org.springframework.stereotype.Repository;

import com.garow.data.model.UserSession;

/**
 * 用户 会话 关系 服务
 * @author seg
 *
 */
@Repository
public interface UserSessionSvc {
	void upsert(String uid, String sessionId);

	UserSession findByUserId(String uid);
}
