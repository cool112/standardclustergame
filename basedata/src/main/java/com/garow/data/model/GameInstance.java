package com.garow.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.garow.data.service.GameInstanceSvc;

/**
 * 游戏实例,用于做消费信号量
 * 
 * @author seg
 *
 */
@Document(collection = "gameinstance")
public class GameInstance {
	@Id
	String id;
	/** 游戏进行的状态*/
	int status = GameInstanceSvc.STATUS_START;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
