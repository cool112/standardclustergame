package com.garow.notifyserver.proto.conn;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.data.mongo.MongoSession;

import com.garow.auth.service.AuthServiceContext;
import com.garow.auth.utils.SessionUtils;
import com.garow.net.socket.annotation.BzPacket;
import com.garow.net.socket.constants.PacketType;
import com.garow.notifyserver.cache.UserStateCache;
import com.garow.notifyserver.constants.UserState;
import com.garow.notifyserver.event.AbstractPushEvent;
import com.garow.notifyserver.service.NotifyServiceContext;
import com.garow.proto.PacketIds;
import com.garow.proto.conn.Online.OnlineEvent;
import com.garow.route.packet.AbstractSessionRecvPacket;
/**
 * 上线事件
 * @author seg
 *
 */
@BzPacket(id = PacketIds.ONLINE, type = PacketType.GATEWAY_NOTIFY)
public class OnlineRecvPacket extends AbstractSessionRecvPacket<OnlineEvent> {
	private static final Logger LOG = LoggerFactory.getLogger(OnlineRecvPacket.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -8139351569622657230L;

	@Override
	protected void readParamsImpl(byte[] dst) throws Exception {
		data = OnlineEvent.parseFrom(dst);
	}

	@Override
	public int getPacketId() {
		return PacketIds.ONLINE;
	}

	@Override
	protected void runImpl() {
		MongoSession session = AuthServiceContext.getMongoOperationsSessionRepository().findById(sessionId);
		if(session == null)
			return;
		LOG.info("online event:" + sessionId);
		String user = session.getAttribute(SessionUtils.KEY_USER);
		String app = session.getAttribute(SessionUtils.KEY_APP);
		UserStateCache.updateUser(sessionId, app, user, UserState.ONLINE);
		Collection<AbstractPushEvent> delayEvents = UserStateCache.removeDelayEvents(app, user);
		if (delayEvents.isEmpty())
			return;
		for (AbstractPushEvent event : delayEvents) {
			NotifyServiceContext.getPushQueueService().addTask(event);
		}
	}

}
