package com.garow.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.garow.data.model.GameInstance;
/**
 * 游戏实例集合
 * @author seg
 *
 */
public interface GameInstanceRepo extends MongoRepository<GameInstance, String> {

}
