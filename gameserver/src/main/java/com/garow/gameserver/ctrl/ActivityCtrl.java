package com.garow.gameserver.ctrl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garow.auth.utils.SessionUtils;
import com.garow.base.constants.ErrorStatus;
import com.garow.base.view.ErrorCode;
import com.garow.base.view.activity.LoginDayInfoView;
import com.garow.data.model.LoginDayInfo;
import com.garow.data.model.User;
import com.garow.gameserver.service.ActivityService;

/**
 * 活动接口
 * 
 * @author seg
 *
 */
@RestController
@RequestMapping("/activity")
public class ActivityCtrl {
	@Resource
	private ActivityService activityService;
	/**
	 * 查询登录奖励
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryLoginDay")
	public ErrorCode queryLoginDay(HttpServletRequest request) {
		User user = SessionUtils.getUser(request.getSession());
		if (user == null)
			return new ErrorCode(ErrorStatus.INVALID_SESSION, "no user");
		LoginDayInfo loginDayInfo = activityService.queryLoginDay(user.getDeviceId(), user.getFirstApp());
		if (loginDayInfo == null)
			return new ErrorCode(ErrorStatus.INVALID_PARAMS, "login day info is null");
		// view
		LoginDayInfoView view = activityService.buildRewardView(loginDayInfo);
		return new ErrorCode(0, view);
	}
	/**
	 * 领取登录奖励
	 * @param time
	 * @param request
	 * @return
	 */
	@RequestMapping("/recvLoginReward")
	public ErrorCode receiveLoginReward(String time, HttpServletRequest request) {
		User user = SessionUtils.getUser(request.getSession());
		if (user == null)
			return new ErrorCode(ErrorStatus.INVALID_SESSION, "no user");
		if (activityService.receiveReward(user, time))
			return new ErrorCode();

		return new ErrorCode(ErrorStatus.REPEAT_REQUEST, "no reward");
	}
}
