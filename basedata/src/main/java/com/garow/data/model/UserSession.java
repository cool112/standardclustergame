package com.garow.data.model;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户和session关系,5分钟后过期
 * 
 * @author seg
 *
 */
@Document(collection = "user_session")
public class UserSession {
	@Indexed(unique = true)
	String	userId;
	String	sessionId;
	@Indexed(expireAfterSeconds = 300)
	Date	lastModify;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getLastModify() {
		return lastModify;
	}

	public void setLastModify(Date lastModify) {
		this.lastModify = lastModify;
	}

}
