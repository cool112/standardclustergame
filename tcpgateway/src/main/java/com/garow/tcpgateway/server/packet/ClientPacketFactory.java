package com.garow.tcpgateway.server.packet;

import org.springframework.stereotype.Component;

import com.garow.net.socket.constants.PacketType;
import com.garow.net.socket.packet.AbstractPacketFactory;
import com.garow.net.socket.packet.AbstractRecvPacket;
/**
 * 客户端包工厂
 * @author seg
 *
 */
@Component
public class ClientPacketFactory extends AbstractPacketFactory<AbstractRecvPacket> {
	@Override
	protected PacketType getRcvPacketType() {
		return PacketType.CLIENT_GATEWAY;
	}

	@Override
	protected Class<AbstractRecvPacket> getPacketSupperClass() {
		return AbstractRecvPacket.class;
	}

}
