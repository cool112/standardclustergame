package com.garow.tcpgateway.proto.auth;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.data.mongo.MongoSession;

import com.garow.auth.service.AuthServiceContext;
import com.garow.net.socket.annotation.BzPacket;
import com.garow.net.socket.constants.ContextKeys;
import com.garow.net.socket.constants.PacketType;
import com.garow.net.socket.packet.AbstractRecvPacket;
import com.garow.net.socket.server.config.ProtoConfigContext;
import com.garow.proto.PacketIds;
import com.garow.proto.auth.HeartOuterClass.Heart;
import com.garow.proto.utils.ProtobufUtil;
/**
 * 心跳业务,心跳不需要很频繁，根据客户端需要判断网络状况的需求，小于5分钟的均可
 * @author seg
 *
 */
@BzPacket(id = PacketIds.HEART, type = PacketType.CLIENT_GATEWAY)
public class HeartRecvPacket extends AbstractRecvPacket<Heart> {
	private static final Logger	LOG					= LoggerFactory.getLogger(HeartRecvPacket.class);
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4035764620937134233L;

	@Override
	protected void readParamsImpl(byte[] dst) throws Exception {
		data = Heart.parseFrom(dst);
	}

	@Override
	public int getPacketId() {
		return PacketIds.HEART;
	}

	@Override
	protected void runImpl() {
		String sessionId = getCtx().channel().attr(ContextKeys.KEY_SESSION).get();
		if (sessionId == null)
			return;
		LOG.info("session=%s, time=%d", sessionId, data.getTimestamp());
		MongoSession session = AuthServiceContext.getMongoOperationsSessionRepository().findById(sessionId);
		if (session == null) {
			getCtx().close();// session超时,需要重新建立全局session,而不仅是长连接,所有使用session的业务都要注意
			return;
		}
		long curTime = System.currentTimeMillis();
		session.setExpireAt(new Date(curTime + ProtoConfigContext.getServerConfig().getSessionTimeout()));
		AuthServiceContext.getMongoOperationsSessionRepository().save(session);

		getCtx().channel()
				.write(ProtobufUtil.toByteArray(PacketIds.HEART, Heart.newBuilder().setTimestamp(curTime).build()));
	}

}
