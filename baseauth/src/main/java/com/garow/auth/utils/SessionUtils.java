package com.garow.auth.utils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.garow.data.model.Player;
import com.garow.data.model.User;
import com.garow.data.service.PlayerSvc;
import com.garow.data.service.UserSvc;

/**
 * session工具
 * 
 * @author seg
 *
 */
@Component
public class SessionUtils {
	public static final String KEY_USER = "user";
	public static final String KEY_APP = "app";
	@Autowired(required = true)
	private PlayerSvc playerSvc;
	private static PlayerSvc mPlayerSvc;
	@Autowired
	private UserSvc userSvc;
	private static UserSvc mUserSvc;
	@PostConstruct     
	  private void initStaticSvc () {
		mPlayerSvc = this.playerSvc;
		mUserSvc = this.userSvc;
	  }
	
	/**
	 * 通过session获取玩家信息
	 * @param session
	 * @return
	 */
	public static Player getPlayer(HttpSession session) {
		if (session == null)
			return null;
		Object user = session.getAttribute(KEY_USER);
		if (user == null)
			return null;
		Object app = session.getAttribute(KEY_APP);
		if (app == null)
			return null;
		return mPlayerSvc.findPlayerByDevAndApp((String) user, (String) app);
	}
	/**
	 * 获取user对象
	 * @param session
	 * @return
	 */
	public static User getUser(HttpSession session) {
		if (session == null)
			return null;
		Object user = session.getAttribute(KEY_USER);
		if (user == null)
			return null;
		Object app = session.getAttribute(KEY_APP);
		if (app == null)
			return null;
		return mUserSvc.findByDeviceIdAndAppExists((String) user, (String) app);
	}
	
}
