package com.garow.route.packet;

import com.google.protobuf.GeneratedMessageV3;
import com.garow.net.socket.packet.AbstractRecvPacket;

/**
 * 抽象session接收包
 * 
 * @author seg
 *
 * @param <T>
 */
public abstract class AbstractSessionRecvPacket<T extends GeneratedMessageV3> extends AbstractRecvPacket<T> {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -753338636524907615L;
	protected String			sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
