package com.garow.proto;

/**
 * 业务包id
 * 
 */
public interface PacketIds {
	// ****************AUTH*******************//
	short	INDEX_1		= ModuleIds.AUTH;
	/** 验证身份 */
	short	AUTH		= 1001;
	/** 心跳 */
	short	HEART		= 1002;
	// ****************TRANSFER*******************//
	short	INDEX_2		= ModuleIds.TRANSFER;
	/** 声明连接的客户端 */
	short	KNOCK		= 2001;
	/** 发送给客户端的封包 */
	short	TO_CLIENT	= 2002;
	/** 服务器处理的包 */
	short	TO_SERVER	= 2003;
	// ****************CONN*******************//
	short	INDEX_3		= ModuleIds.CONN;
	/** 客户端离线 */
	short	OFFLINE		= 3001;
	/** 客户端online */
	short	ONLINE		= 3002;
	// ****************PUSH*******************//
	short	INDEX_4		= ModuleIds.PUSH;
	short	MESSAGE		= 4001;
	// ****************COMMON*******************//
	short	INDEX_5		= ModuleIds.COMMON;
	/** 通用回应 */
	short	ACK			= 5001;
}
