package com.garow.route.web.ctrl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garow.auth.utils.SessionUtils;
import com.garow.base.view.ErrorCode;
import com.garow.base.view.InstanceInfo;
import com.garow.data.service.UserSessionSvc;
import com.garow.net.socket.client.config.ClientConfig;
import com.garow.net.socket.client.service.ConnectService;
import com.garow.route.cache.RouteManager;
import com.garow.route.config.RouteConfig;
import com.garow.route.service.AbstractRouteEventHandler;
import com.garow.route.service.RouteServiceContext;

import io.netty.channel.ChannelHandler;

/**
 * 路由接口
 * 
 * @author seg
 *
 */
@RestController
@RequestMapping("/route")
public class RouteCtrl {
	private static final Logger			LOG	= org.slf4j.LoggerFactory.getLogger(RouteCtrl.class);
	@Resource
	private RouteConfig					routeConfig;
	@Resource
	private UserSessionSvc				userSessionSvc;
	@Autowired
	private AbstractRouteEventHandler	routeEventHandler;
	/**
	 * 通知会话路由绑定事件
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/notify")
	public String notifyRoute(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String sessionId = session.getId();
		routeEventHandler.onUserBind(sessionId, session, routeConfig.getApps());
		return "success";
	}

	/**
	 * 改变session,暂时没用
	 * 
	 * @param oldSession
	 * @param request
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/changeSession")
	public String notifyChangeSession(String oldSession, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String sessionId = session.getId();
		RouteManager.changeSession(oldSession, sessionId);
		return "success";
	}
	/**
	 * 会话离线，目前策略是用户登录时更新usr-session，该接口暂时也没用
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/offline")
	public String offlineSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String sessionId = session.getId();
		Object userId = session.getAttribute(SessionUtils.KEY_USER);
		if (userId == null)
			return "user null";
		userSessionSvc.upsert((String) userId, sessionId);
		return "success";
	}
	/**
	 * 请求进行长连接
	 * @param tcpHost
	 * @return
	 */
	@RequestMapping(value = "/connect")
	public ErrorCode connectTo(String tcpHost) {
		RouteServiceContext.getInternalConnectService().connect(tcpHost);
		return new ErrorCode();
	}
}
