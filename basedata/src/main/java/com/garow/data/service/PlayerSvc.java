package com.garow.data.service;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.garow.data.model.Player;
/**
 * 玩家数据服务
 * @author seg
 *
 */
@Repository
public interface PlayerSvc {
	/**
	 * 通过设备号和应用获取玩家
	 * @param devId
	 * @param appId
	 * @return
	 */
	Player findPlayerByDevAndApp(String devId, String appId);
	
	void update(Player player);
	/**
	 * 注册新玩家
	 * @param player
	 */
	void newPlayer(Player player);
	/**
	 * 获取玩家在不同应用的集合,统一用户密码
	 * @param devId
	 * @return
	 */
	List<Player> findByDevId(String devId);
	/**
	 * 更新玩家分数
	 * @param player
	 * @return
	 */
	boolean updateScore(Player player);
}
