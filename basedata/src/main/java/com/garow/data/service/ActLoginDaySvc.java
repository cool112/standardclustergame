package com.garow.data.service;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.garow.data.model.LoginDayInfo;
/**
 * 活动-登录奖励服务
 * @author seg
 *
 */
@Repository
public interface ActLoginDaySvc {
	public LoginDayInfo findByAppAndUser(String app, String userId);
	public LoginDayInfo findByAppAndUserIdAndEndTimeGreaterThan(String app, String userId, Date curTime);
	public boolean updateRewardState(LoginDayInfo loginDay);
	public LoginDayInfo addNew(LoginDayInfo loginDay);
	public boolean findAndModify(LoginDayInfo oldLoginDay, LoginDayInfo newLoginDay);
}
