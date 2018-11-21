package com.garow.tcpgateway.proto.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.data.mongo.MongoSession;

import com.garow.auth.service.AuthServiceContext;
import com.garow.base.constants.ErrorStatus;
import com.garow.net.socket.annotation.BzPacket;
import com.garow.net.socket.constants.ContextKeys;
import com.garow.net.socket.constants.PacketType;
import com.garow.net.socket.packet.AbstractRecvPacket;
import com.garow.proto.PacketIds;
import com.garow.proto.auth.AuthOuterClass.Auth;
import com.garow.proto.common.AckOuterClass.Ack;
import com.garow.proto.common.AckOuterClass.Ack.Builder;
import com.garow.proto.utils.ProtobufUtil;
import com.garow.tcpgateway.cache.ClientConnectManager;
import com.garow.tcpgateway.server.service.TcpGatewayServiceContext;
/**
 * 验证业务
 * @author seg
 *
 */
@BzPacket(id = PacketIds.AUTH, type=PacketType.CLIENT_GATEWAY)
public class AuthRecvPacket extends AbstractRecvPacket<Auth> {
	private static final Logger LOG = LoggerFactory.getLogger(AuthRecvPacket.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -7674254708116804701L;

	@Override
	protected void readParamsImpl(byte[] dst) throws Exception{
			data = Auth.parseFrom(dst);
	}

	@Override
	public int getPacketId() {
		return PacketIds.AUTH;
	}

	@Override
	protected void runImpl() {
		String sessionId = data.getSessionId();
		MongoSession session = AuthServiceContext.getMongoOperationsSessionRepository().findById(sessionId);
		int errCode = ErrorStatus.INVALID_SESSION;
		if(session != null) {
			LOG.info("auth session:" + session.getAttributeNames());
			if(getCtx().channel().attr(ContextKeys.KEY_SESSION).compareAndSet(null, sessionId))
				errCode = ErrorStatus.SUC;
			ClientConnectManager.addConnect(sessionId, getCtx().channel());
			getCtx().channel().attr(ContextKeys.KEY_AUTH).compareAndSet(false, true);
		}
		Builder builder = Ack.newBuilder();
		builder.setErrCode(errCode);
		builder.setProId(getPacketId());
		getCtx().channel().writeAndFlush(ProtobufUtil.toByteArray(PacketIds.ACK, builder.build()));
		TcpGatewayServiceContext.getConnectEventService().fireOnline(sessionId);
		
	}

}
