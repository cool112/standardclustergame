package com.garow.notifyserver.task;

import java.util.Collection;

import com.garow.notifyserver.cache.UserStateCache;
import com.garow.notifyserver.cache.UserStateEntity;
import com.garow.notifyserver.constants.UserState;
import com.garow.notifyserver.event.AbstractPushEvent;
import com.garow.notifyserver.service.NotifyServiceContext;

/**
 * 超时后保存推送事件
 * 
 * @author seg
 *
 */
public class OfflineWaitTask implements Runnable {
	/** 等待时间 */
	public static final long	WAIT_TIMEOUT	= 300000;
	private String				app;
	private String				user;
	/**给超时条件一点偏差值*/
	private long				delay;

	public OfflineWaitTask(String app, String user, long delay) {
		super();
		this.app = app;
		this.user = user;
		this.delay = delay - 100;
	}

	@Override
	public void run() {
		UserStateEntity userState = UserStateCache.getUserState(app, user);
		if (userState == null)
			return;
		long curTime = System.currentTimeMillis();
		if (curTime - userState.getLastModTime() < delay)
			return;
		UserStateCache.updateUser(userState.getSessionId(), app, user, UserState.OFFLINE);
		Collection<AbstractPushEvent> delayEvents = UserStateCache.removeDelayEvents(app, user);
		if (delayEvents.isEmpty())
			return;
		for (AbstractPushEvent event : delayEvents) {
			NotifyServiceContext.getPushQueueService().addTask(event);
		}
	}

}
