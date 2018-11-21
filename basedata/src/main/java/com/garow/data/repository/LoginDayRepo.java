package com.garow.data.repository;

import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.garow.data.model.LoginDayInfo;

public interface LoginDayRepo extends MongoRepository<LoginDayInfo, String> {
	LoginDayInfo findFristByAppAndUserId(String app, String userId);
	public LoginDayInfo findFirstByAppAndUserIdAndEndTimeGreaterThan(String app, String userId, Date curTime);
}
