package com.garow.data.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.garow.data.model.Rank;
/**
 * 排行榜数据
 * @author seg
 *
 */
public interface RankRepo extends MongoRepository<Rank, String> {
	List<Rank> findTop10ByAppOrderByScoreDesc(String app);
}
