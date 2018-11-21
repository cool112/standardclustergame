package com.garow.notifyserver.event;

import com.garow.notifyserver.constants.UserState;
/**
 * 抽象推送事件
 * @author seg
 *
 */
public abstract class AbstractPushEvent implements Runnable{
	@Override
	public void run() {
		int cond = condition();
		switch (cond) {
		case UserState.ONLINE:
			send();
			if(isPersistent())
				remove();
			break;
		case UserState.WAIT:
			delay();
			break;
		case UserState.OFFLINE:
			save();
			break;
		default:
			break;
		}
	}
	/**
	 * 发送
	 */
	protected abstract void send();
	/**
	 * 移除
	 */
	protected abstract void remove();
	/**
	 * 推迟
	 */
	protected abstract void delay();
	/**
	 * 储存
	 */
	protected abstract void save();
	/**
	 * 获取状态
	 * @return
	 */
	protected abstract int condition();
	/**
	 * 是否是持久化加载的推送
	 * @return
	 */
	protected abstract boolean isPersistent();
}
