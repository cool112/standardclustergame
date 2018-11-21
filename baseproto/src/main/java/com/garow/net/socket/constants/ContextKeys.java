/**
 * 
 */
package com.garow.net.socket.constants;

import io.netty.util.AttributeKey;

/**
 * channel attr key
 * @author gjs
 * 2018年7月24日
 */
public interface ContextKeys {
	/** auth */
	AttributeKey<Boolean> KEY_AUTH = AttributeKey.newInstance("auth");
	/** 最后活跃时间 限制无session超时时间*/
	AttributeKey<Long> KEY_LAST_LIVE = AttributeKey.newInstance("lastLive");
	/** sessionid */
	AttributeKey<String> KEY_SESSION = AttributeKey.newInstance("session");
}
