package com.garow.logserver.service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.garow.logserver.generator.entity.CommonLog;
import com.garow.logserver.generator.mapper.CommonLogMapper;
import com.garow.logserver.task.CommonLogInsertTask;

/**
 * 公共日志服务
 * 
 * @author seg
 *
 */
@Service
public class CommonLogService {
	private static final Logger				LOG			= LoggerFactory.getLogger(CommonLogService.class);
	@Resource
	private CommonLogMapper					commonLogMapper;
	@Resource
	private ThreadPoolTaskExecutor			threadPool;

	@Resource
	private ScheduledExecutorService		scheduler;

	private LinkedBlockingQueue<CommonLog>	logQueue	= new LinkedBlockingQueue<CommonLog>(100000);

	@PostConstruct
	void init() {
		threadPool.execute(new CommonLogInsertTask(threadPool, scheduler, logQueue, commonLogMapper));
	}

	public void insertOne(CommonLog log) {
		try {
			if (!logQueue.offer(log, 5, TimeUnit.SECONDS))
			{
				LOG.error("commonlog queue is full!");
				LOG.warn(JSON.toJSONString(log));
			}
		} catch (InterruptedException e) {
			LOG.error("commonlog queue is full!", e);
		}
	}
	/**
	 * 批量插入，队列满时有5秒等待，然后日志会被写文件
	 * @param logs
	 */
	public void insertBatch(CommonLog... logs) {
		try {
			for (CommonLog log : logs) {
				if (!logQueue.offer(log, 5, TimeUnit.SECONDS)) {
					LOG.error("commonlog queue is full!");
					LOG.warn(JSON.toJSONString(log));
				}
			}
		} catch (InterruptedException e) {
			LOG.error("commonlog queue is full!", e);
		}
	}

}
