package com.garow.gameserver.ctrl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garow.auth.utils.SessionUtils;
import com.garow.base.constants.ErrorStatus;
import com.garow.base.utils.WebUtils;
import com.garow.base.view.ErrorCode;
import com.garow.data.model.AppInfo;
import com.garow.data.model.GameInstance;
import com.garow.data.model.User;
import com.garow.data.service.GameInstanceSvc;
import com.garow.data.service.RankSvc;
import com.garow.data.service.UserSvc;

/**
 * 用户api
 * 
 * @author seg
 *
 */
@RestController
@RequestMapping("/user")
public class UserCtrl {
	@Resource
	private UserSvc			userSvc;
	@Autowired
	private GameInstanceSvc	gameInstanceSvc;
	@Autowired
	private RankSvc			rankSvc;
	/**
	 * 注册
	 * @param user
	 * @param app
	 * @param pwd
	 * @return
	 */
	@RequestMapping("/register")
	public String register(String user, String app, String pwd) {
		if (StringUtils.isEmpty(user) || StringUtils.isEmpty(app))
			return "invalid parameters";
		User userEntity = userSvc.findByDeviceId(user);
		if (userEntity == null) {
			userEntity = new User();
			userEntity.setDeviceId(user);
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
				userSvc.updatePlayer(user, app, appInfo);
			} else {
				return "registered";
			}
		}
		return "success";
	}
	/**
	 * 第三方查询玩家信息
	 * @param deviceId
	 * @param app
	 * @param params
	 * @return
	 */
	@RequestMapping("/getOne")
	public AppInfo getOne(String deviceId, String app, @RequestBody(required = false) Map<String, Object> params) {
		if (!WebUtils.checkParamters(params, deviceId, app))
			return null;
		if (deviceId == null)
			deviceId = (String) params.get("deviceId");
		if (app == null)
			app = (String) params.get("app");
		return userSvc.findApp(deviceId, app);
	}
	/**
	 * 玩家自查信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/appInfo")
	public AppInfo appInfo(HttpServletRequest request) {
		User user = SessionUtils.getUser(request.getSession());
		if (user == null)
			return new AppInfo();
		String appName = user.getFirstApp();
		if (appName == null)
			return new AppInfo();
		AppInfo app = user.getPlayers().get(appName);
		return app;
	}

	/**
	 * 更新玩家分数,需要对应游戏流水号,防止重复提交和恶意提交
	 * 
	 * @param gameId
	 * @param score
	 * @param request
	 * @return
	 */
	@RequestMapping("/updateScore")
	public ErrorCode updateScore(String gameId, int score, HttpServletRequest request) {
		User user = SessionUtils.getUser(request.getSession());
		if (user == null)
			return new ErrorCode(ErrorStatus.INVALID_PARAMS, "user null");
		GameInstance game = gameInstanceSvc.findById(gameId);
		if (game == null || game.getStatus() >= GameInstanceSvc.STATUS_END)
			return new ErrorCode(ErrorStatus.REPEAT_REQUEST, "game end");
		String appName = user.getFirstApp();
		if (appName == null)
			return new ErrorCode(ErrorStatus.INVALID_PARAMS, "app null");
		AppInfo app = user.getPlayers().get(appName);
		int oldScore = app.getScore();
		if (userSvc.updateScore(user.getDeviceId(), appName, score)) {
			rankSvc.updateScore(appName, oldScore, score);
			game.setStatus(GameInstanceSvc.STATUS_END);
			gameInstanceSvc.updateStatus(game);
		}
		return new ErrorCode();
	}

	/**
	 * 做md5校验，deviceId+app+archive
	 * 
	 * @param md5
	 * @param archive
	 * @param request
	 * @return
	 */
	@RequestMapping("/save")
	public ErrorCode archive(String md5, String archive, HttpServletRequest request) {
		User user = SessionUtils.getUser(request.getSession());
		if (user == null)
			return new ErrorCode(ErrorStatus.INVALID_PARAMS, "user null");
		String appName = user.getFirstApp();
		if (appName == null)
			return new ErrorCode(ErrorStatus.INVALID_PARAMS, "app null");
		StringBuilder signStr = new StringBuilder();
		signStr.append(user.getDeviceId()).append(appName).append(archive);
		String digest = DigestUtils.md5DigestAsHex(signStr.toString().getBytes());
		if (!md5.equals(digest))
			return new ErrorCode(ErrorStatus.INVALID_PARAMS, "sign fail!");
		userSvc.saveArchive(user.getDeviceId(), appName, archive);
		return new ErrorCode();
	}
}
