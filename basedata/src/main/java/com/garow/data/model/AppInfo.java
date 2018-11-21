package com.garow.data.model;

import java.io.Serializable;
import java.util.Date;

/**
 * app info，应用/游戏类型信息
 * 
 * @author seg
 *
 */
public class AppInfo implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7996417435425207733L;
	/** 玩家昵称 */
	String						name				= "";
	/** 分数 */
	int							score;
	/** 存档数据，目前用字符串保存，方便http传输 */
	String						archive				= "";
	/** 更新时间 */
	Date						updateTime			= new Date();
	/** 版本号，方便客户端判断 */
	int							vers;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getVers() {
		return vers;
	}

	public void setVers(int vers) {
		this.vers = vers;
	}

}
