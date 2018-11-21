package com.garow.data.service;

import org.springframework.stereotype.Repository;

import com.garow.data.model.GameInstance;

/**
 * 游戏战斗实例
 * @author seg
 *
 */
@Repository
public interface GameInstanceSvc {
	/** 开始 */
	int	STATUS_START	= 0;
	/** 结束 */
	int	STATUS_END		= 1;

	/**
	 * 新增游戏实例
	 * 
	 * @param game
	 * @return uuid
	 */
	String insert(GameInstance game);

	/**
	 * 查询实例
	 * 
	 * @param id
	 * @return 实例
	 */
	GameInstance findById(String id);

	/**
	 * 改变实例状态
	 * 
	 * @param game
	 */
	void updateStatus(GameInstance game);
}
