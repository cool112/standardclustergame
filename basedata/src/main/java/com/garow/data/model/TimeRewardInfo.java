package com.garow.data.model;
/**
 * 按时间获取奖励的类型
 * @author seg
 *
 */
public class TimeRewardInfo {
	/**时间key*/
	String	time = "";
	/**是否领取*/
	boolean	received;
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

}
