package com.garow.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.garow.data.model.CommonLog;
/**
 * 公共日志仓库 commonLog 集合
 * @author seg
 *
 */
public interface CommonLogRepo extends MongoRepository<CommonLog, String> {
}
