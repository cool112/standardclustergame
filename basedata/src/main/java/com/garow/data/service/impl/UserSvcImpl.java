package com.garow.data.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.garow.data.model.AppInfo;
import com.garow.data.model.User;
import com.garow.data.repository.UserRepo;
import com.garow.data.service.UserSvc;
import com.mongodb.client.result.UpdateResult;

@Service
public class UserSvcImpl implements UserSvc {
	@Resource
	private UserRepo		userRepo;
	@Resource
	private MongoTemplate	mongoTemplate;

	@Override
	public AppInfo findApp(String deviceId, String app) {
		User user = findByDeviceIdAndAppExists(deviceId, app);
		return user.getPlayers().get(app);
	}

	@Override
	public User findByDeviceIdAndAppExists(String deviceId, String app) {
		String appKey = "players." + app;
		Query query = new Query(Criteria.where("deviceId").is(deviceId).and(appKey).exists(true));
		query.fields().include(appKey).include("deviceId");
		List<User> users = mongoTemplate.find(query, User.class);
		if (CollectionUtils.isEmpty(users))
			return null;

		User user = users.get(0);
		return user;
	}

	@Override
	public void updatePlayer(String deviceId, String app, AppInfo info) {
		Query query = new Query(Criteria.where("deviceId").is(deviceId));
		Update update = new Update().set("players." + app, info);
		mongoTemplate.upsert(query, update, "user");
	}

	@Override
	public User findByDeviceId(String deviceId) {
		List<User> users = userRepo.findByDeviceId(deviceId);
		if (CollectionUtils.isEmpty(users))
			return null;
		return users.get(0);
	}

	@Override
	public void newUser(User user) {
		userRepo.save(user);
	}

	@Override
	public boolean updateScore(String userId, String app, int score) {
		AppInfo oldApp = findApp(userId, app);
		if(oldApp == null)
			return false;
		if(!checkScoreRule(oldApp.getScore(), score))
			return false;
		UpdateResult result = userUpdate(userId, app, "score", score);
		if(result.isModifiedCountAvailable() && result.getModifiedCount() > 1)
			return true;
		return false;
	}
	/**
	 * score update rule，目前只更新更高的分数
	 * @param oldScore
	 * @param newScore
	 * @return
	 */
	private boolean checkScoreRule(int oldScore, int newScore) {
		return oldScore <= newScore;
	}

	@Override
	public boolean saveArchive(String userId, String app, String archive) {
		UpdateResult result = userUpdate(userId, app, "archive", archive);
		if(result.isModifiedCountAvailable() && result.getModifiedCount() > 1)
			return true;
		return false;
	}
	/**
	 * 封装更新操作，包含对更新时间和版本号的自动更新
	 * @param userId
	 * @param app
	 * @param appKey
	 * @param val
	 * @return
	 */
	private UpdateResult userUpdate(String userId, String app, String appKey, Object val) {
		String appPref = "players." + app;
		Query query = new Query(Criteria.where("deviceId").is(userId).and(appPref).exists(true));
		Update update = new Update().set(appPref+"."+appKey, val);
		return userUpdate(appPref, query, update);
	}
	
	private UpdateResult userUpdate(String appPref, Query query, Update update) {
		update = update.currentDate(appPref+".updateTime").inc(appPref+".vers", 1);
		return mongoTemplate.updateFirst(query, update, User.class);
	}

}
