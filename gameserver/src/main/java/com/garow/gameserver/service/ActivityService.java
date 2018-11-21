package com.garow.gameserver.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.garow.base.pojo.config.ItemMeta;
import com.garow.base.pojo.config.RewardData;
import com.garow.base.view.activity.LoginDayInfoView;
import com.garow.base.view.activity.RewardSlotView;
import com.garow.data.model.LoginDayInfo;
import com.garow.data.model.TimeRewardInfo;
import com.garow.data.model.User;
import com.garow.data.service.ActLoginDaySvc;
import com.garow.gameserver.config.ActivityConfig;

/**
 * 活动服务
 * 
 * @author seg
 *
 */
@Service
public class ActivityService {
	private static final Logger				LOG		= LoggerFactory.getLogger(ActivityService.class);
	private static final SimpleDateFormat	FORMAT	= new SimpleDateFormat("yyyy-MM-dd");
	@Resource
	private ActLoginDaySvc					actLoginDaySvc;
	@Resource
	private ActivityConfig					activityConfig;
	/**
	 * 查询登录奖励信息
	 * @param userId
	 * @param app
	 * @return
	 */
	public LoginDayInfo queryLoginDay(String userId, String app) {
		Date curDate = new Date();
		LoginDayInfo loginDayInfo = actLoginDaySvc.findByAppAndUser(app, userId);
		if (loginDayInfo == null) {
			try {
				loginDayInfo = newLoginDay(app, userId);
				if (loginDayInfo == null)
					return null;
				actLoginDaySvc.addNew(loginDayInfo);
			} catch (Exception e) {
				LOG.error("repest insert loginday", e);
				return null;
			}

		}
		loginDayInfo = checkReset(loginDayInfo, curDate);
		loginDayInfo = checkReward(loginDayInfo, curDate);
		return loginDayInfo;

	}
	/**
	 * 检查奖励是否新增
	 * @param loginDayInfo
	 * @param curDate
	 * @return
	 */
	private LoginDayInfo checkReward(LoginDayInfo loginDayInfo, Date curDate) {
		if (DateUtils.isSameDay(loginDayInfo.getVisitTime(), curDate))
			return loginDayInfo;
		for (TimeRewardInfo rewardInfo : loginDayInfo.getRewardList()) {
			if (StringUtils.isEmpty(rewardInfo.getTime())) {
				rewardInfo.setTime(FORMAT.format(curDate));
				break;
			}
		}
		LoginDayInfo newLoginInfo = loginDayInfo;
		if (!actLoginDaySvc.findAndModify(loginDayInfo, newLoginInfo)) {
			return actLoginDaySvc.findByAppAndUser(loginDayInfo.getApp(), loginDayInfo.getUserId());
		}
		return newLoginInfo;
	}
	/**
	 * 检查奖励是否重置
	 * @param loginDayInfo
	 * @param curDate
	 * @return
	 */
	private LoginDayInfo checkReset(LoginDayInfo loginDayInfo, Date curDate) {
		if (loginDayInfo.getEndTime().after(curDate))
			return loginDayInfo;

		LoginDayInfo newLoginDay = newLoginDay(loginDayInfo.getApp(), loginDayInfo.getUserId());
		if (!actLoginDaySvc.findAndModify(loginDayInfo, newLoginDay))
			return actLoginDaySvc.findByAppAndUser(loginDayInfo.getApp(), loginDayInfo.getUserId());

		return newLoginDay;
	}
	/**
	 * 创建新实体并初始化
	 * @param app
	 * @param userId
	 * @return
	 */
	private LoginDayInfo newLoginDay(String app, String userId) {
		LoginDayInfo newInfo = new LoginDayInfo();
		newInfo.setApp(app);
		newInfo.setUserId(userId);
		Date curDate = new Date();
		newInfo.setVisitTime(curDate);
		newInfo.setStartTime(curDate);
		List<RewardData> rewardList = GameServiceContext.getDataInitService().getLoginCountData().getAppMap().get(app);
		Date endDate = null;
		if (activityConfig.getLogindayReset() > 0) {
			endDate = DateUtils.addDays(curDate, activityConfig.getLogindayReset());
		} else {
			endDate = new Date(Long.MAX_VALUE);
		}
		newInfo.setEndTime(endDate);
		if (CollectionUtils.isEmpty(rewardList)) {
			LOG.error("login day activity config error: rewardList is null");
			return null;
		}
		List<TimeRewardInfo> rewardInfoList = new ArrayList<TimeRewardInfo>();
		for (RewardData rewardData : rewardList) {
			rewardInfoList.add(new TimeRewardInfo());
		}
		rewardInfoList.get(0).setTime(FORMAT.format(curDate));
		newInfo.setRewardList(rewardInfoList);
		return newInfo;
	}

	/**
	 * 领取奖励
	 * 
	 * @param user
	 * @param time
	 * @return
	 */	
	public boolean receiveReward(User user, String time) {
		String userId = user.getDeviceId();
		String app = user.getFirstApp();
		LoginDayInfo loginDayInfo = actLoginDaySvc.findByAppAndUser(app, userId);
		if (loginDayInfo == null) {
			return false;
		}
		TimeRewardInfo targetReward = null;
		for (TimeRewardInfo rewardInfo : loginDayInfo.getRewardList()) {
			if (rewardInfo.getTime().equals(time)) {
				targetReward = rewardInfo;
				break;
			}
		}
		if(targetReward == null || targetReward.isReceived())
			return false;
		targetReward.setReceived(true);
		if (!actLoginDaySvc.findAndModify(loginDayInfo, loginDayInfo))
			return false;
		// exec reward and notify
		return true;
	}

	/**
	 * 构建返回视图
	 * 
	 * @param loginDayInfo
	 * @return
	 */
	public LoginDayInfoView buildRewardView(LoginDayInfo loginDayInfo) {
		LoginDayInfoView view = new LoginDayInfoView();
		view.setStartTime(loginDayInfo.getStartTime().getTime());
		view.setVisitTime(loginDayInfo.getVisitTime().getTime());
		view.setEndTime(loginDayInfo.getEndTime().getTime());
		List<RewardSlotView> rewards = new ArrayList<RewardSlotView>();

		for (int i = 0; i < loginDayInfo.getRewardList().size(); ++i) {
			TimeRewardInfo reward = loginDayInfo.getRewardList().get(i);
			RewardData rewardData = GameServiceContext.getDataInitService().getLoginCountData().getAppMap()
					.get(loginDayInfo.getApp()).get(i);
			ItemMeta itemMeta = GameServiceContext.getDataInitService().getItemData().getItemMap()
					.get(rewardData.getCategory()).get(rewardData.getId());
			RewardSlotView rewardView = new RewardSlotView();
			rewardView.setKey(reward.getTime());
			rewardView.setReceived(reward.isReceived());
			rewardView.setRewards(GameServiceContext.getDataInitService().transfer(itemMeta));
			rewards.add(rewardView);
		}
		view.setRewardList(rewards);
		return view;
	}
}
