package com.garow.notifyserver.constants;
/**
 * 用户状态
 * @author seg
 *
 */
public interface UserState {
	/**初始*/
	int	INIT	= 0;
	/**在线*/
	int	ONLINE	= 1;
	/**等待上线*/
	int	WAIT	= 2;
	/**离线*/
	int	OFFLINE	= 3;
}
