package com.garow.route.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.data.mongo.MongoSession;

import com.garow.auth.service.AuthServiceContext;
import com.garow.base.view.InstanceInfo;
import com.garow.route.cache.RouteManager;

/**
 * 抽象路由事件处理
 * @author seg
 *
 */
public abstract class AbstractRouteEventHandler {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractRouteEventHandler.class);
	/**
	 * 通知应用会话绑定
	 * @param sessionId
	 * @param session
	 * @param apps
	 */
	public void onUserBind(String sessionId, HttpSession session, List<String> apps) {
		MongoSession _session = null;
		for(String app : apps) {
			InstanceInfo host = (InstanceInfo) (_session == null?session.getAttribute(app):_session.getAttribute(app));
			if(host == null) {
				LOG.warn(app+" attr is null");
				if(_session == null) {
					_session = AuthServiceContext.getMongoOperationsSessionRepository().findById(sessionId);
					host = _session.getAttribute(app);
					session.setAttribute(app, host);
				}
				if(host == null) {
					LOG.warn(app+" attr is null in db");
					continue;
				}
			}
			RouteManager.changeRoute(sessionId, app,  host.getTcpHost());
		}
		onUserBindImpl(sessionId, session);
	}
	/**
	 * 自定义的绑定事件处理
	 * @param sessionId
	 * @param session 
	 */
	protected abstract void onUserBindImpl(String sessionId, HttpSession session);
}
