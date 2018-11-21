package com.garow.net.socket.constants;

/**
 * 包类型,由于使用相同的id,需要通过类型来区分通道
 * @author gjs
 *
 */
public enum PacketType {
	/** 客户端到网关 */
	CLIENT_GATEWAY,
	/** 内部通讯 */
	INTERNAL,
	/** 网关到推送服 */
	GATEWAY_NOTIFY,
	;
	
}
