package com.garow.notifyserver.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.garow.notifyserver.task.OfflineWaitTask;

/**
 * 推送队列服务
 * 
 * @author seg
 *
 */
@Service
public class PushQueueService {
	private static final Logger			LOG	= LoggerFactory.getLogger(PushQueueService.class);
	@Resource
	private ThreadPoolTaskExecutor		threadPool;
	@Resource
	private ScheduledExecutorService	scheduler;

	public void schedule(Runnable run, long delaySec) {
		scheduler.schedule(run, delaySec, TimeUnit.MILLISECONDS);
	}

	public void addTask(Runnable task) {
		threadPool.execute(task);
	}

	/**
	 * 开始等待超时任务
	 * 
	 * @param app
	 * @param userId
	 */
	public void startWaitTimeout(String app, String userId) {
		schedule(new OfflineWaitTask(app, userId, OfflineWaitTask.WAIT_TIMEOUT), OfflineWaitTask.WAIT_TIMEOUT);
	}
}