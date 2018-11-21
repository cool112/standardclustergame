package com.garow.data.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * 登录奖励数据
 * @author seg
 *
 */
@Document(collection = "act_login_day")
@CompoundIndex(def = "{'app':1,'userId':1}", unique = true)
public class LoginDayInfo {
	/**应用名*/
	String				app;
	/**用户id*/
	String				userId;
	/**最后修改数据的时间*/
	Date				visitTime;
	/**活动开始时间*/
	Date				startTime;
	/**活动结束时间，无限则是最大值*/
	Date				endTime;
	/**奖励领取情况以及总共的奖励数*/
	List<TimeRewardInfo>	rewardList;

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Date visitTime) {
		this.visitTime = visitTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public List<TimeRewardInfo> getRewardList() {
		return rewardList;
	}

	public void setRewardList(List<TimeRewardInfo> rewardList) {
		this.rewardList = rewardList;
	}

}
