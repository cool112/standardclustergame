package com.garow.data.service;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.garow.data.model.Rank;

/**
 * 排行榜服务
 * @author seg
 *
 */
@Repository
public interface RankSvc {
	/**
	 * 获取分数在全体的排名,并列不分先后
	 * @param app
	 * @param score
	 * @return
	 */
	int getGlobalRank(String app, int score);
	/**
	 * 获取前10个排名分(忽略并列的玩家)
	 * @param app
	 * @return
	 */
	List<Rank> getTop10Rank(String app);
	/**
	 * 分数变更
	 * @param app
	 * @param oldScore
	 * @param newScore
	 * @return
	 */
	@Transactional
	boolean updateScore(String app, int oldScore, int newScore);
}
