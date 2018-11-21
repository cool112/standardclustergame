package com.garow.base.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.garow.base.service.DataRefreshService;
/**
 * 数据刷新监听，对应/actuator/refresh事件
 * @author seg
 *
 */
@Component
public class DataRefreshEventListener implements ApplicationListener<EnvironmentChangeEvent> {
	@Autowired(required = false)
	private DataRefreshService dateRefreshService;

	@Override
	public void onApplicationEvent(EnvironmentChangeEvent event) {
		if (dateRefreshService == null)
			return;

		dateRefreshService.refresh();
	}

}
