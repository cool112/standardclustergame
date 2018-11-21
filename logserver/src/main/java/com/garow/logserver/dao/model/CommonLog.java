package com.garow.logserver.dao.model;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
/**
 * 建表用实体
 * @author seg
 *
 */
@Table(name = "tb_common_log")
public class CommonLog {
	/** 占位 用户id */
	@Column(name = "uid", type = MySqlTypeConstant.VARCHAR, length = 50)
	String	uid;
	/** 设备id */
	@Column(name = "device_id", type = MySqlTypeConstant.VARCHAR, length = 50)
	String	deviceId;
	/** 日志类型 */
	@Column(name = "log_type", type = MySqlTypeConstant.INT)
	int		logType;
	/** 附带消息 */
	@Column(name = "msg", type = MySqlTypeConstant.VARCHAR, length = 200)
	String	msg;
	/** 时间戳 */
	@Column(name = "time", type = MySqlTypeConstant.BIGINT)
	long	time;
	/** 应用id */
	@Column(name = "app", type = MySqlTypeConstant.VARCHAR, length = 50)
	String	app;
	/** 渠道id */
	@Column(name = "chan_id", type = MySqlTypeConstant.VARCHAR, length = 50)
	String	chanId;

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

}
