package com.garow.data.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.garow.data.model.User;
/**
 * 用户主体集合
 * @author seg
 *
 */
public interface UserRepo extends MongoRepository<User, String>{
	List<User> findByDeviceId(String deviceId);
}
