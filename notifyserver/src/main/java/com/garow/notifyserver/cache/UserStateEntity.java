package com.garow.notifyserver.cache;
/**
 * 用户状态实体
 * @author seg
 *
 */
public class UserStateEntity {
	int		state;
	/**最后修改时间*/
	long	lastModTime;
	/**当前绑定的session*/
	String	sessionId;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getLastModTime() {
		return lastModTime;
	}

	public void setLastModTime(long lastModTime) {
		this.lastModTime = lastModTime;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
