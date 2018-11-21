package com.garow.data.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 权限校验，及应用信息
 * 
 * @author seg
 *
 */
@Document
@CompoundIndex(def = "{uid:1,deviceId:1,players:1}")
public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -584172585054901019L;
	/** 占位 用户id */
	String			uid;
	/** 设备id */
	@Indexed(unique = true)
	String			deviceId;
	String			password	= "";
	/**
	 * players.appName=appInfo
	 */
	Map<String, AppInfo>	players;
	public String getFirstApp() {
		if(players == null || players.isEmpty())
			return null;
		return players.keySet().iterator().next();
	}
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<String, AppInfo> getPlayers() {
		return players;
	}

	public void setPlayers(Map<String, AppInfo> players) {
		this.players = players;
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
	

}
