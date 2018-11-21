package com.garow.route.proto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.garow.net.socket.annotation.BzPacket;
import com.garow.net.socket.constants.PacketType;
import com.garow.net.socket.packet.AbstractRecvPacket;
import com.garow.proto.PacketIds;
import com.garow.proto.transfer.KnockOuterClass.Knock;
import com.garow.route.cache.InternalConnectManager;

/**
 * 内部服务器间认证协议
 * @author seg
 *
 */
@BzPacket(id = PacketIds.KNOCK, type = PacketType.INTERNAL)
public class KnockRecvPacket extends AbstractRecvPacket<Knock> {
	private static final Logger	LOG					= LoggerFactory.getLogger(KnockRecvPacket.class);
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3505111955830645194L;

	@Override
	protected void readParamsImpl(byte[] dst) throws Exception {
		data = Knock.parseFrom(dst);
	}

	@Override
	public int getPacketId() {
		return PacketIds.KNOCK;
	}

	@Override
	protected void runImpl() {
		String host = data.getClientHost();
		InternalConnectManager.addConnect(host, getCtx().channel());
		LOG.info("establish connect with:" + host);
	}

}
