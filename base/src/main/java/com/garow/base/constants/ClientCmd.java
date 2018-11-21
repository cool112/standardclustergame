package com.garow.base.constants;
/**
 * 客户端控制命令
 * @author seg
 *
 */
public interface ClientCmd {
	/**转发*/
	int TRANSFER = 0;
	/**关闭连接*/
	int CLOSE = 1;
	/**转发完关闭连接*/
	int TRANSFER_AND_CLOSE = 2;
	/**全服转发,需要考虑应用过滤,或者由推送服去过滤,分批发送*/
	int ALL = 3;
}
