package com.garow.logserver.task;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.garow.logserver.generator.entity.CommonLog;
import com.garow.logserver.generator.mapper.CommonLogMapper;

/**
 * 公共日志插入任务
 * @author seg
 *
 */
public class CommonLogInsertTask extends AbstractInsertTask<CommonLog> {
	private static final Logger	LOG	= LoggerFactory.getLogger(CommonLogInsertTask.class);
	private CommonLogMapper		commonLogMapper;

	public CommonLogInsertTask(ThreadPoolTaskExecutor threadPool, ScheduledExecutorService scheduler,
			LinkedBlockingQueue<CommonLog> queue, CommonLogMapper commonLogMapper) {
		super(threadPool, scheduler, queue);
		this.commonLogMapper = commonLogMapper;
	}

	@Override
	protected void runImpl(int logCount, List<CommonLog> logList) throws Exception {
		LOG.info("commonLog insert task count:" + logCount);
		commonLogMapper.insertBatch(logList);
		LOG.info("commonLog insert task end");
	}

}
