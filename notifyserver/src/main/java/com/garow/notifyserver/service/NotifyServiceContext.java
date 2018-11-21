package com.garow.notifyserver.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.garow.notifyserver.packet.NotifyRecvPacketFactory;

@Component
public class NotifyServiceContext {
	@Resource
	private PushQueueService		pushQueueService;
	private static PushQueueService	mPushQueueService;
	@Resource
	private NotifyRecvPacketFactory notifyRecvPacketFactory;
	private static NotifyRecvPacketFactory mNotifyRecvPacketFactory;

	@PostConstruct
	void init() {
		mPushQueueService = pushQueueService;
		mNotifyRecvPacketFactory = notifyRecvPacketFactory;
	}

	public static PushQueueService getPushQueueService() {
		return mPushQueueService;
	}

	public static NotifyRecvPacketFactory getNotifyRecvPacketFactory() {
		return mNotifyRecvPacketFactory;
	}
	

}
