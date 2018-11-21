package com.garow.notifyserver.service;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.garow.auth.utils.SessionUtils;
import com.garow.base.service.BaseServiceContext;
import com.garow.base.view.ErrorCode;
import com.garow.base.view.InstanceInfo;
import com.garow.net.web.service.ProtoServiceContext;
import com.garow.notifyserver.cache.UserStateCache;
import com.garow.notifyserver.constants.UserState;
import com.garow.route.cache.InternalConnectManager;
import com.garow.route.service.AbstractRouteEventHandler;
import com.garow.route.utils.InternalPacketUtil;

import io.netty.channel.Channel;

/**
 * 推送服路由事件处理
 * 
 * @author seg
 *
 */

@Service
public class NotifyRouteEventHandler extends AbstractRouteEventHandler {
	private static final Logger LOG = LoggerFactory.getLogger(NotifyRouteEventHandler.class);

	@Override
	protected void onUserBindImpl(String sessionId, HttpSession session) {
		String user = (String) session.getAttribute(SessionUtils.KEY_USER);
		String app = (String) session.getAttribute(SessionUtils.KEY_APP);
		LOG.info("bind session:" + sessionId);
		UserStateCache.updateUser(sessionId, app, user, UserState.WAIT);// user may not connect tcp gateway
		NotifyServiceContext.getPushQueueService().startWaitTimeout(app, user);
		InstanceInfo gatewayInfo = (InstanceInfo) session.getAttribute(InternalPacketUtil.GATEWAY_APP);
		if (gatewayInfo == null) {
			LOG.warn("tcp gateway info is null! session:" + sessionId);
			return;
		}
		Channel ch = InternalConnectManager.getChannel(gatewayInfo.getTcpHost());
		if (ch != null && ch.isWritable())
			return;
		ResponseEntity<ErrorCode> resp = BaseServiceContext
				.getHttpService().get(
						"http://" + gatewayInfo.getHttpHost() + "/route/connect?tcpHost="
								+ ProtoServiceContext.getInstanceService().getTcpHost(),
						null, sessionId, ErrorCode.class);
		LOG.info("request gateway to estblish connection:" + (resp.hasBody() ? resp.getBody().getStatus() : "null"));
	}

}
