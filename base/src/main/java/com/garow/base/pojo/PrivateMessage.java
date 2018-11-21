package com.garow.base.pojo;
/**
 * 私人消息
 * @author seg
 *
 */
public class PrivateMessage {
	/**目标*/
	String user;
	String app;
	String from;
	String msg;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
