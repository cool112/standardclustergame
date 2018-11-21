package com.garow.data.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.garow.data.model.Message;
import com.garow.data.repository.MessageRepo;
import com.garow.data.service.MessageSvc;
@Service
public class MessageSvcImpl implements MessageSvc {
	@Autowired
	private MessageRepo messageRepo;
	@Override
	public List<Message> findByUserAndAppOrderByTime(String user, String app) {
		return messageRepo.findByUserAndAppOrderByTime(user, app);
	}
	@Override
	public void save(Message message) {
		messageRepo.save(message);
	}
	@Override
	public void deleteById(String id) {
		messageRepo.deleteById(id);
	}

}
