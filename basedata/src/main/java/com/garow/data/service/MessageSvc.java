package com.garow.data.service;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.garow.data.model.Message;

/**
 * 私信服务
 * 
 * @author seg
 *
 */
@Repository
public interface MessageSvc {
	List<Message> findByUserAndAppOrderByTime(String user, String app);

	void save(Message message);

	void deleteById(String id);
}
