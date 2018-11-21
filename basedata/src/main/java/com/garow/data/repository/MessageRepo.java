package com.garow.data.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.garow.data.model.Message;
/**
 * 私信集合
 * @author seg
 *
 */
public interface MessageRepo extends MongoRepository<Message, String> {
 List<Message> findByUserAndAppOrderByTime(String user, String app);
}
