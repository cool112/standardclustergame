package com.garow.tcpgateway.server.service;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.garow.route.service.AbstractRouteEventHandler;
/**
 * 网关的路由事件处理
 * @author seg
 *
 */
@Service
public class GatewayRouteEventHandler extends AbstractRouteEventHandler {

	@Override
	protected void onUserBindImpl(String sessionId, HttpSession session) {
		//do nothing
	}

}
