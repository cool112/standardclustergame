package com.garow.data.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.garow.data.model.UserSession;
import com.garow.data.repository.UserSessionsRepo;
import com.garow.data.service.UserSessionSvc;
import com.mongodb.client.result.UpdateResult;
/**
 * 
 * @author seg
 *
 */
@Service
public class UserSessionSvcImpl implements UserSessionSvc {
	private static final Logger LOG = LoggerFactory.getLogger(UserSessionSvcImpl.class);
	@Autowired
	private UserSessionsRepo userSessionsRepo;
	@Autowired
	private MongoTemplate mongoTempalte;
	@Override
	public void upsert(String uid, String sessionId) {
		Query query = new Query(Criteria.where("userId").is(uid));
		Update update = new Update().set("sessionId", sessionId).currentDate("lastModify");
		UpdateResult result = mongoTempalte.upsert(query, update, UserSession.class);
		if(!result.isModifiedCountAvailable() || result.getModifiedCount() <= 0)
			LOG.warn("user session upsert fail! uid=%s, session=%s", uid, sessionId);
	}
	@Override
	public UserSession findByUserId(String uid) {
		return userSessionsRepo.findByUserId(uid);
	}

}
