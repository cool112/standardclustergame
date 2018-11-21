package com.garow.notifyserver.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.garow.data.service.DataServiceContext;
import com.garow.notifyserver.cache.UserStateCache;
import com.garow.notifyserver.cache.UserStateEntity;
import com.garow.notifyserver.constants.UserState;
import com.garow.proto.PacketIds;
import com.garow.proto.push.MessageOuterClass.Message;
import com.garow.proto.utils.ProtobufUtil;
import com.garow.route.utils.InternalPacketUtil;

/**
 * 个人私信
 * 
 * @author seg
 *
 */
public class PrivateMessageEvent extends AbstractPushEvent {
	private static final Logger	LOG	= LoggerFactory.getLogger(PrivateMessageEvent.class);
	private String				user;
	private String				app;
	private String				from;
	private String				msg;
	private long				time;
	private String				sessionId;
	private boolean				isPersistent;
	private String				id;

	public PrivateMessageEvent(String user, String app, String from, String msg, long time) {
		super();
		this.user = user;
		this.app = app;
		this.from = from;
		this.msg = msg;
		this.time = time;
	}

	public PrivateMessageEvent(com.garow.data.model.Message message) {
		this.user = message.getUser();
		this.app = message.getApp();
		this.from = message.getFrom();
		this.msg = message.getMsg();
		this.time = message.getTime();
		isPersistent = true;
		this.id = message.getId();
	}

	@Override
	protected void send() {
		LOG.info("send msg");
		Message message = Message.newBuilder().setFrom(from).setName(from).setMsg(msg).setTime(time).build();
		InternalPacketUtil.sendGateway(sessionId, ProtobufUtil.toByteArray(PacketIds.MESSAGE, message));
	}

	@Override
	protected void delay() {
		LOG.info("delay msg");
		UserStateCache.addDelayEvent(app, user, this);
	}

	@Override
	public void save() {
		LOG.info("save msg");
		com.garow.data.model.Message message = new com.garow.data.model.Message();
		message.setApp(app);
		message.setFrom(from);
		message.setMsg(msg);
		message.setUser(user);
		message.setTime(time);
		DataServiceContext.getMessageSvc().save(message);
	}

	@Override
	protected int condition() {
		UserStateEntity userState = UserStateCache.getUserState(app, user);
		if (userState == null)
			return UserState.OFFLINE;
		sessionId = userState.getSessionId();
		return userState.getState();
	}

	@Override
	protected boolean isPersistent() {
		return isPersistent;
	}

	public void setPersistent(boolean isPersistent) {
		this.isPersistent = isPersistent;
	}

	@Override
	protected void remove() {
		LOG.info("remove msg");
		DataServiceContext.getMessageSvc().deleteById(id);
	}

}
