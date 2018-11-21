package com.garow.base.view.activity;

import java.util.List;

/**
 * 登录天数信息
 * 
 * @author seg
 *
 */
public class LoginDayInfoView {
	long visitTime;
	long startTime;
	long endTime;
	List<RewardSlotView> rewardList;

	public long getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(long visitTime) {
		this.visitTime = visitTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public List<RewardSlotView> getRewardList() {
		return rewardList;
	}

	public void setRewardList(List<RewardSlotView> rewardList) {
		this.rewardList = rewardList;
	}

}
