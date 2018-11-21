package com.garow.logserver.ctrl;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garow.base.constants.ErrorStatus;
import com.garow.base.view.ErrorCode;
import com.garow.logserver.generator.entity.CommonLog;
import com.garow.logserver.service.CommonLogService;

/**
 * 日志api
 * 
 * @author seg
 *
 */
@RestController()
@RequestMapping("/log")
public class LogCtrl {
	@Resource
	private CommonLogService commonLogService;
	/**
	 * 插入一条
	 * @param log
	 * @return
	 */
	@RequestMapping("/insertOne")
	public ErrorCode oneLog(@RequestBody CommonLog log) {
		if (log == null)
			return new ErrorCode(ErrorStatus.INVALID_PARAMS, "log is null");
		log.setTime(System.currentTimeMillis());
		commonLogService.insertOne(log);
		return new ErrorCode();
	}
	/**
	 * 插入多条
	 * @param logs
	 * @return
	 */
	@RequestMapping("/insertBatch")
	public ErrorCode batchLogs(@RequestBody CommonLog[] logs) {
		if (logs == null)
			return new ErrorCode(ErrorStatus.INVALID_PARAMS, "log is null");
		long curTime = System.currentTimeMillis();
		for (CommonLog log : logs) {
			log.setTime(curTime);
		}
		commonLogService.insertBatch(logs);
		return new ErrorCode();
	}
}
