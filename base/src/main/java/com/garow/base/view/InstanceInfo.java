package com.garow.base.view;

import java.io.Serializable;

/**
 * 实例信息
 * 
 * @author seg
 *
 */
public class InstanceInfo implements Serializable {
	/**
		 * 
		 */
	private static final long	serialVersionUID	= -7250576848607525839L;
	/** http监听 */
	String						httpHost;
	/** tcp监听 */
	String						tcpHost;
	/** tcp外网监听 */
	String						tcpWanHost;

	public String getHttpHost() {
		return httpHost;
	}

	public void setHttpHost(String httpHost) {
		this.httpHost = httpHost;
	}

	public String getTcpHost() {
		return tcpHost;
	}

	public void setTcpHost(String tcpHost) {
		this.tcpHost = tcpHost;
	}

	public String getTcpWanHost() {
		return tcpWanHost;
	}

	public void setTcpWanHost(String tcpWanHost) {
		this.tcpWanHost = tcpWanHost;
	}
	
}
