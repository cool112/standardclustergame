package com.garow.data.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;

/**
 * 玩家实体，暂时废弃的玩家信息数据结构
 * 
 * @author seg
 *
 */
@Deprecated
@CompoundIndex(def="{'uid':1,'devicId':1,'app':1}", unique = true)
public class Player implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3692760751183402248L;
	@Id
	String id;
	/** 占位 用户id*/
	String uid;
	/** 设备id*/
	String deviceId;
	/** 应用id*/
	String app;
	/**历史最高分*/
	int score;
	/** 存档数据,根据客户端要求调整*/
	String archive;
	/**最后修改时间*/
	Date updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getArchive() {
		return archive;
	}

	public void setArchive(String archive) {
		this.archive = archive;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
