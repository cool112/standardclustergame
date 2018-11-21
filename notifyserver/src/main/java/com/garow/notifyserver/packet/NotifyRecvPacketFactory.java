package com.garow.notifyserver.packet;

import org.springframework.stereotype.Component;

import com.garow.net.socket.constants.PacketType;
import com.garow.net.socket.packet.AbstractPacketFactory;
import com.garow.route.packet.AbstractSessionRecvPacket;
/**
 * 推送服接收包工厂
 * @author seg
 *
 */

@SuppressWarnings("rawtypes")
@Component
public class NotifyRecvPacketFactory extends AbstractPacketFactory<AbstractSessionRecvPacket> {

	@Override
	protected PacketType getRcvPacketType() {
		return PacketType.GATEWAY_NOTIFY;
	}

	@Override
	protected Class<AbstractSessionRecvPacket> getPacketSupperClass() {
		return AbstractSessionRecvPacket.class;
	}

}
