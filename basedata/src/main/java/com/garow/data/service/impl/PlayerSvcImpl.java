package com.garow.data.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.garow.data.model.Player;
import com.garow.data.repository.PlayerRepo;
import com.garow.data.service.PlayerSvc;
@Service
public class PlayerSvcImpl implements PlayerSvc {
	@Autowired
	private PlayerRepo playerRepo;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Override
	public Player findPlayerByDevAndApp(String devId, String appId) {
		return playerRepo.findOneByDeviceIdAndApp(devId, appId);
	}
	@Override
	public void update(Player player) {
		Player oldPlayer = findPlayerByDevAndApp(player.getDeviceId(), player.getApp());
		if (!checkVersion(oldPlayer, player)) {
			return;
		}
		playerRepo.save(player);
	}
	@Override
	public void newPlayer(Player player) {
		playerRepo.insert(player);
	}
	@Override
	public List<Player> findByDevId(String devId) {
		return playerRepo.findByDeviceId(devId);
	}
	@Override
	public boolean updateScore(Player player) {
		Player oldPlayer = findPlayerByDevAndApp(player.getDeviceId(), player.getApp());
		if(!checkVersion(oldPlayer, player))
			return false;
		if(!checkScoreRule(oldPlayer, player))
			return false;
		Query query = new Query(Criteria.where("deviceId").is(player.getDeviceId()).and("app").is(player.getApp()));
		Update update = new Update().set("score", player.getScore()).currentDate("updateTime");
		mongoTemplate.updateFirst(query, update, Player.class);
		return true;
	}
	/**
	 * 检查数据版本
	 * @param old
	 * @param cur
	 * @return
	 */
	private boolean checkVersion(Player old, Player cur) {
		if (old == null || old.getUpdateTime().after(cur.getUpdateTime())) {
			return false;
		}
		return true;
	}
	/**
	 * 检查更新分数规则,目前分数是递增的
	 * @param old
	 * @param cur
	 * @return
	 */
	private boolean checkScoreRule(Player old, Player cur) {
		if (old == null || old.getScore() >= cur.getScore()) {
			return false;
		}
		return true;
	}
}
