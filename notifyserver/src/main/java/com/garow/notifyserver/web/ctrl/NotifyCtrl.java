package com.garow.notifyserver.web.ctrl;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garow.base.pojo.PrivateMessage;
import com.garow.base.view.ErrorCode;
import com.garow.notifyserver.event.PrivateMessageEvent;
import com.garow.notifyserver.service.NotifyServiceContext;

/**
 * 推送服api
 * 
 * @author seg
 *
 */
@RestController
@RequestMapping("/notify")
public class NotifyCtrl {
	/**
	 * 私信
	 * @return
	 */
	@RequestMapping("/privateMessage")
	public ErrorCode privateMessage(@RequestBody PrivateMessage message) {
		PrivateMessageEvent event = new PrivateMessageEvent(message.getUser(), message.getApp(), message.getFrom(),
				message.getMsg(), System.currentTimeMillis());
		NotifyServiceContext.getPushQueueService().addTask(event);
		return new ErrorCode();
	}
}
