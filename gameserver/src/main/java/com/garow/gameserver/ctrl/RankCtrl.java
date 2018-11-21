package com.garow.gameserver.ctrl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garow.data.model.Rank;
import com.garow.data.service.RankSvc;

/**
 * 排行榜api
 * 
 * @author seg
 *
 */
@RestController
@RequestMapping("/rank")
public class RankCtrl {
	@Autowired
	private RankSvc rankSvc;
	/**
	 * 全局排名
	 * @param app
	 * @param score
	 * @return
	 */
	@RequestMapping("/globalRank")
	public int globalRank(String app, int score) {
		return rankSvc.getGlobalRank(app, score);
	}
	/**
	 * 排行榜前10分数
	 * @param app
	 * @return
	 */
	@RequestMapping("/top10")
	public List<Rank> getTopX(String app) {
		return rankSvc.getTop10Rank(app);
	}
}
