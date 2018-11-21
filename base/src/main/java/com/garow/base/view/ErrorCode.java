package com.garow.base.view;

import java.io.Serializable;

/**
 * 错误码
 * 
 * @author seg
 *
 */
public class ErrorCode implements Serializable {
	/**
		 * 
		 */
	private static final long	serialVersionUID	= 7213668596121132602L;
	/**状态码*/
	int							status;
	/**消息体*/
	Object						msg;
	
	public ErrorCode() {
		super();
	}

	public ErrorCode(int status, Object msg) {
		super();
		this.status = status;
		this.msg = msg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}


}
