package com.garow.logserver.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 抽象插入任务，批量插入，无任务时自动退避
 * 
 * @author seg
 *
 * @param <T>
 */
public abstract class AbstractInsertTask<T> implements Runnable {
	private static final Logger			LOG			= LoggerFactory.getLogger(AbstractInsertTask.class);
	/**最大批量插入数*/
	private static final int			MAX_COUNT	= 1000;
	/**无任务时的等待时间，秒*/
	private static final int			WAIT_TIME	= 5;
	private ThreadPoolTaskExecutor		threadPool;
	private ScheduledExecutorService	scheduler;
	private LinkedBlockingQueue<T>		queue;

	public AbstractInsertTask(ThreadPoolTaskExecutor threadPool, ScheduledExecutorService scheduler,
			LinkedBlockingQueue<T> queue) {
		super();
		this.threadPool = threadPool;
		this.scheduler = scheduler;
		this.queue = queue;
	}

	@Override
	public void run() {
		List<T> logList = new ArrayList<T>();
		int logCount = queue.drainTo(logList, MAX_COUNT);
		if (logCount <= 0) {
			scheduler.schedule(new Runnable() {

				@Override
				public void run() {
					threadPool.execute(AbstractInsertTask.this);
				}
			}, WAIT_TIME, TimeUnit.SECONDS);
		} else {
			try {
				runImpl(logCount, logList);
			} catch (Exception e) {
				LOG.error("insert task run fail!", e);
			}
			if (logCount < MAX_COUNT) {
				scheduler.schedule(new Runnable() {

					@Override
					public void run() {
						threadPool.execute(AbstractInsertTask.this);
					}
				}, WAIT_TIME, TimeUnit.SECONDS);
			} else {
				threadPool.execute(this);
			}
		}
	}
	/**
	 * 插入实现
	 * @param logCount
	 * @param logList
	 * @throws Exception
	 */
	protected abstract void runImpl(int logCount, List<T> logList) throws Exception;

}
