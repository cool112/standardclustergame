package com.garow.data.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.garow.data.model.LoginDayInfo;
import com.garow.data.repository.LoginDayRepo;
import com.garow.data.service.ActLoginDaySvc;
import com.mongodb.client.result.UpdateResult;
/**
 * activity login day count 
 * @author seg
 *
 */
@Service
public class ActLoginDaySvcImpl implements ActLoginDaySvc {
	@Resource
	private LoginDayRepo loginDayRepo;
	@Resource
	private MongoTemplate mongoTemplate;
	@Override
	public LoginDayInfo findByAppAndUser(String app, String userId) {
		return loginDayRepo.findFristByAppAndUserId(app, userId);
	}

	@Override
	public boolean updateRewardState(LoginDayInfo loginDay) {
		Query query = new Query(Criteria.where("app").is(loginDay.getApp()).and("userId").is(loginDay.getUserId()));
		Update update = new Update().set("rewardList", loginDay.getRewardList()).currentDate("visitTime");
		UpdateResult result = mongoTemplate.updateFirst(query, update, LoginDayInfo.class);
		if(result.isModifiedCountAvailable() && result.getMatchedCount() > 0)
			return true;
		return false;
	}

	@Override
	public LoginDayInfo addNew(LoginDayInfo loginDay) {
		if(loginDay == null)
			return null;
		return loginDayRepo.insert(loginDay);
	}

	@Override
	public LoginDayInfo findByAppAndUserIdAndEndTimeGreaterThan(String app, String userId, Date curTime) {
		return loginDayRepo.findFirstByAppAndUserIdAndEndTimeGreaterThan(app, userId, curTime);
	}

	@Override
	public boolean findAndModify(LoginDayInfo oldLoginDay, LoginDayInfo newLoginDay) {
		Query query = new Query(Criteria.where("app").is(oldLoginDay.getApp()).and("userId").is(oldLoginDay.getUserId()).and("visitTime").is(oldLoginDay.getVisitTime()));
		Update update = new Update().currentDate("visitTime").set("rewardList", newLoginDay.getRewardList()).set("startTime", newLoginDay.getStartTime()).set("endTime", newLoginDay.getEndTime());
		FindAndModifyOptions options = new FindAndModifyOptions();
		options.upsert(false);
		options.returnNew(true);
		options.remove(false);
		LoginDayInfo result = mongoTemplate.findAndModify(query, update, options, LoginDayInfo.class);
		if(result == null)
			return false;
		return true;
	}

}
