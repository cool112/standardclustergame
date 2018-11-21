package com.garow.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 通用日志,目前转移到日志服提供该服务
 * 
 * @author seg
 *
 */
@Deprecated
@Document(collection="commonlog")
@CompoundIndex(def="{'uid':1,'deviceId':1,'app':1}")
public class CommonLog {
	@Id
	String id;
	/** 占位 用户id*/
	String uid;
	/** 设备id*/
	String deviceId;
	/** 日志类型*/
	int logType;
	/** 附带消息*/
	String msg;
	/** 时间戳*/
	@Indexed
	long time;
	/** 应用id*/
	String app;
	/** 渠道id*/
	String chanId;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getLogType() {
		return logType;
	}

	public void setLogType(int logType) {
		this.logType = logType;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getChanId() {
		return chanId;
	}

	public void setChanId(String chanId) {
		this.chanId = chanId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
