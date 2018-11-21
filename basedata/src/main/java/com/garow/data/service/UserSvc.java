package com.garow.data.service;

import org.springframework.stereotype.Repository;

import com.garow.data.model.AppInfo;
import com.garow.data.model.User;
/**
 * 用户服务
 * @author seg
 *
 */
@Repository
public interface UserSvc {
	/**
	 * 用户信息，并过滤其他app信息
	 * @param deviceId
	 * @param app
	 * @return
	 */
	User findByDeviceIdAndAppExists(String deviceId, String app);
	
	AppInfo findApp(String deviceId, String app);
	
	void updatePlayer(String deviceId, String app, AppInfo info);
	
	User findByDeviceId(String deviceId);
	
	void newUser(User user);
	/**
	 * 更新分数，受分数规则检验
	 * @param userId
	 * @param app
	 * @param score
	 * @return
	 */
	boolean updateScore(String userId, String app, int score);
	/**
	 * 存档
	 * @param userId
	 * @param app
	 * @param archive
	 * @return
	 */
	boolean saveArchive(String userId, String app, String archive);
	
}
