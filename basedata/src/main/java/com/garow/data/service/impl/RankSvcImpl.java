package com.garow.data.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.garow.data.model.Rank;
import com.garow.data.repository.RankRepo;
import com.garow.data.service.RankSvc;
import com.garow.data.view.GlobalRank;
/**
 * 排行榜实现
 * @author seg
 *
 */
@Service
public class RankSvcImpl implements RankSvc {
	@Autowired
	private RankRepo rankRepo;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Override
	public int getGlobalRank(String app, int score) {
		MatchOperation match = Aggregation.match(Criteria.where("app").is(app).and("score").gt(score));
		GroupOperation group = Aggregation.group("app").sum("count").as("ahead");
		 TypedAggregation<Rank> agg = Aggregation.newAggregation(Rank.class, match, group);
		AggregationResults<GlobalRank> result = mongoTemplate.aggregate(agg, GlobalRank.class);
		if (result.getMappedResults().isEmpty())
			return Integer.MAX_VALUE;
		GlobalRank globalRank = result.getMappedResults().get(0);
		return globalRank.getAhead() + 1;
	}

	@Override
	public List<Rank> getTop10Rank(String app) {
		return rankRepo.findTop10ByAppOrderByScoreDesc(app);
	}

	@Override
	public boolean updateScore(String app, int oldScore, int newScore) {
		Query query = new Query(Criteria.where("app").is(app).and("score").is(oldScore));
		Update update = new Update().inc("count", -1);
		mongoTemplate.upsert(query, update, Rank.class);
		query = new Query(Criteria.where("app").is(app).and("score").is(newScore));
		update = new Update().inc("count", 1);
		mongoTemplate.upsert(query, update, Rank.class);
		return true;
	}

}
