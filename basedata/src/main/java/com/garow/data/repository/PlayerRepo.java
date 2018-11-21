package com.garow.data.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.garow.data.model.Player;

/**
 * player 集合<br>
 * 规则参考:https://docs.spring.io/spring-data/data-mongo/docs/1.4.1.RELEASE/reference/htmlsingle/#repositories.query-methods.query-creation
 * @author seg
 *
 */	
public interface PlayerRepo extends MongoRepository<Player, String> {

	Player findOneByDeviceIdAndApp(String deviceId, String app);
	
	List<Player> findByDeviceId(String deviceId);

	@Query(value = "{'uid':?0,'deviceId':?1,'app':?2}")
	List<Player> findByUniqueCond(String uid, String deviceId, String app);
}
