package com.garow.gatewayserver.ctrl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.data.mongo.MongoSession;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.garow.auth.service.AuthServiceContext;
import com.garow.auth.utils.SessionUtils;
import com.garow.base.constants.ErrorStatus;
import com.garow.base.view.ErrorCode;
import com.garow.data.model.AppInfo;
import com.garow.data.model.User;
import com.garow.data.model.UserSession;
import com.garow.data.service.UserSessionSvc;
import com.garow.data.service.UserSvc;

/**
 * 验证服务
 * 
 * @author seg
 *
 */
@RestController
public class AuthCtrl {
	private static final Logger	LOG	= LoggerFactory.getLogger(AuthCtrl.class);
	@Autowired
	private UserSvc				userSvc;
	@Autowired
	private UserSessionSvc		userSessionSvc;

	/**
	 * 帐号注册
	 * 
	 * @param deviceId
	 * @param app
	 * @return
	 */
	@RequestMapping(value = "/preauth", method = { RequestMethod.GET, RequestMethod.POST })
	public ErrorCode preauth(String deviceId, String app, String pwd, @RequestBody Map<String, Object> params) {
		if ((StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(app)) && params.isEmpty())
			return new ErrorCode(ErrorStatus.INVALID_PARAMS, "invalid parameters");
		if (deviceId == null)
			deviceId = (String) params.get("deviceId");
		if (app == null)
			app = (String) params.get("app");
		if (pwd == null)
			pwd = (String) params.get("pwd");
		User userEntity = userSvc.findByDeviceId(deviceId);
		if (userEntity == null) {
			userEntity = new User();
			userEntity.setDeviceId(deviceId);
			if (pwd == null)
				pwd = "";
			userEntity.setPassword(pwd);
			userEntity.setPlayers(new HashMap<String, AppInfo>());
			AppInfo appInfo = new AppInfo();
			userEntity.getPlayers().put(app, appInfo);
			userSvc.newUser(userEntity);
		} else {
			if (userEntity.getPlayers().get(app) == null) {
				AppInfo appInfo = new AppInfo();
				userSvc.updatePlayer(deviceId, app, appInfo);
			} else {
				return new ErrorCode(ErrorStatus.REPEAT_REQUEST, "registered");
			}
		}
		return new ErrorCode();
	}

	/**
	 * 验证+设置用户信息,通过header:sessionid可以维持会话
	 * 
	 * @param deviceId
	 * @param app
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/postauth", method = { RequestMethod.GET, RequestMethod.POST })
	public ErrorCode postauth(String deviceId, String app, @RequestBody Map<String, Object> params,
			HttpServletRequest request) {
		if ((StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(app)) && params.isEmpty())
			return new ErrorCode(ErrorStatus.INVALID_PARAMS, "invalid parameters");
		if (deviceId == null)
			deviceId = (String) params.get("deviceId");
		if (app == null)
			app = (String) params.get("app");
		HttpSession session = request.getSession();
		Object existId = session.getAttribute(SessionUtils.KEY_USER);
		if (existId == null) {
			session.setAttribute(SessionUtils.KEY_USER, deviceId);
			session.setAttribute(SessionUtils.KEY_APP, app);
		} else {
			if (!existId.equals(deviceId)) {
				LOG.error("dev and app mutate:" + existId + " vs " + deviceId);
				return new ErrorCode(ErrorStatus.INVALID_PARAMS, "dev and app mutate");
			}
		}
		String sessionId = session.getId();
		UserSession oldUserSession = userSessionSvc.findByUserId(deviceId);
		boolean relogin = false;
		if(oldUserSession != null) {
			MongoSession oldSession = AuthServiceContext.getMongoOperationsSessionRepository().findById(oldUserSession.getSessionId());
			if(oldSession != null) {
				String appName = oldSession.getAttribute(SessionUtils.KEY_APP);
				if(appName.equals(app)) {
					relogin = true;
					sessionId = oldSession.getId();
				}
			}
		}
		if(!relogin) {
			userSessionSvc.upsert(deviceId, sessionId);
		}

		return new ErrorCode(ErrorStatus.SUC, sessionId);
	}
}
