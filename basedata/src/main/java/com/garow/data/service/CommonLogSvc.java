package com.garow.data.service;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.garow.data.model.CommonLog;

/**
 * 通用日志服务
 * @author seg
 *
 */
@Repository
public interface CommonLogSvc {
	/**
	 * 新增一条日志
	 * @param log
	 */
	void add(CommonLog log);
	/**
	 * 获取所有日志
	 * @return
	 */
	List<CommonLog> getAllLogs();
}
