package com.garow.net.web.ctrl;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garow.base.view.InstanceInfo;
import com.garow.net.web.service.InstanceService;

/**
 * 路由接口
 * 
 * @author seg
 *
 */
@RestController
@RequestMapping("/instance")
public class InstanceCtrl {
	@Resource
	private InstanceService instanceService;

	@RequestMapping(value = "/who")
	public InstanceInfo getInstanceInfo() {
		InstanceInfo info = new InstanceInfo();
		info.setTcpHost(instanceService.getTcpHost());
		info.setHttpHost(instanceService.getHttpHost());
		info.setTcpWanHost(instanceService.getTcpWanHost());
		return info;
	}

	
}
