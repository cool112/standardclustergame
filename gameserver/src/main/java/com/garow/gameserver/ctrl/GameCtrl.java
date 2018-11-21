package com.garow.gameserver.ctrl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.session.data.mongo.MongoSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.garow.auth.service.AuthServiceContext;
import com.garow.base.service.BaseServiceContext;
import com.garow.base.view.InstanceInfo;
import com.garow.data.model.GameInstance;
import com.garow.data.service.GameInstanceSvc;
import com.garow.gameserver.config.RouteConfig;

/**
 * 游戏控制
 * 
 * @author seg
 *
 */
@RestController
@RequestMapping("/game")
public class GameCtrl {
	private static final Logger	LOG	= LoggerFactory.getLogger(GameCtrl.class);
	@Autowired
	private GameInstanceSvc		gameInstanceSvc;
	@Resource
	private RouteConfig			routeConfig;
	@Autowired
	private RestTemplate		restTemplate;
	/**
	 * 创建新战斗，返回流水id
	 * @return
	 */
	@RequestMapping("/new")
	public String newGame() {
		return gameInstanceSvc.insert(new GameInstance());
	}
	/**
	 * 为用户分配tcp路由，返回包含网关的一系列主机地址,之后要改进：
	 * 1、只返回tcp网关地址
	 * 2、提供外网ip
	 * @param request
	 * @return
	 */
	@RequestMapping("/bind")
	public Map<String, InstanceInfo> bindRoute(HttpServletRequest request) {
		Map<String, InstanceInfo> result = new HashMap<String, InstanceInfo>();
		HttpSession session = request.getSession();
		MongoSession underSession = AuthServiceContext.getMongoOperationsSessionRepository().findById(session.getId());
		HttpHeaders headers = new HttpHeaders();
		headers.add("sessionid", session.getId());
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		for (String app : routeConfig.getApps()) {
			ResponseEntity<InstanceInfo> info = restTemplate.exchange("http://" + app.toUpperCase() + "/instance/who",
					HttpMethod.GET, entity, InstanceInfo.class);
			if (!info.hasBody())
				continue;
			InstanceInfo body = info.getBody();
			if(app.equals("tcpgateway"))
				result.put(app, body);
			underSession.setAttribute(app, body);
		}
		AuthServiceContext.getMongoOperationsSessionRepository().save(underSession);
		for (String app : routeConfig.getApps()) {
			InstanceInfo info = (InstanceInfo) underSession.getAttribute(app);
			if (info == null)
				continue;
//			ResponseEntity<String> notifyResult = normalTemplate.exchange("http://"+info.getHttpHost()+"/route/notify",  HttpMethod.GET, entity, String.class);
			ResponseEntity<String> notifyResult = BaseServiceContext.getHttpService()
					.post("http://" + info.getHttpHost() + "/route/notify", null, session.getId(), String.class);
			LOG.info("app: {}, notify: {}", app, notifyResult.getBody());
		}
		return result;
	}
}
