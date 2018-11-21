package com.garow.notifyserver.proto.conn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.data.mongo.MongoSession;

import com.garow.auth.service.AuthServiceContext;
import com.garow.auth.utils.SessionUtils;
import com.garow.net.socket.annotation.BzPacket;
import com.garow.net.socket.constants.PacketType;
import com.garow.notifyserver.cache.UserStateCache;
import com.garow.notifyserver.constants.UserState;
import com.garow.notifyserver.service.NotifyServiceContext;
import com.garow.proto.PacketIds;
import com.garow.proto.conn.Offline.OfflineEvent;
import com.garow.route.packet.AbstractSessionRecvPacket;

/**
 * 离线事件
 * 
 * @author seg
 *
 */
@BzPacket(id = PacketIds.OFFLINE, type = PacketType.GATEWAY_NOTIFY)
public class OfflineRecvPacket extends AbstractSessionRecvPacket<OfflineEvent> {
	private static final Logger	LOG					= LoggerFactory.getLogger(OfflineRecvPacket.class);
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5662774250418243532L;

	@Override
	protected void readParamsImpl(byte[] dst) throws Exception {
		data = OfflineEvent.parseFrom(dst);
	}

	@Override
	public int getPacketId() {
		return PacketIds.OFFLINE;
	}

	@Override
	protected void runImpl() {
		MongoSession session = AuthServiceContext.getMongoOperationsSessionRepository().findById(sessionId);
		if (session == null)
			return;
		LOG.info("offline event:" + sessionId);
		String user = session.getAttribute(SessionUtils.KEY_USER);
		String app = session.getAttribute(SessionUtils.KEY_APP);
		UserStateCache.updateUser(sessionId, app, user, UserState.WAIT);
		NotifyServiceContext.getPushQueueService().startWaitTimeout(app, user);	
	}

}
