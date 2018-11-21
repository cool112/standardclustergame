package com.garow.route.packet;

import org.springframework.stereotype.Component;

import com.garow.net.socket.constants.PacketType;
import com.garow.net.socket.packet.AbstractPacketFactory;
import com.garow.net.socket.packet.AbstractRecvPacket;

/**
 * 内部转发包工厂
 * 
 * @author seg
 *
 */
@Component
public class InternalPacketFactory extends AbstractPacketFactory<AbstractRecvPacket> {

	@Override
	protected PacketType getRcvPacketType() {
		return PacketType.INTERNAL;
	}

	@Override
	protected Class<AbstractRecvPacket> getPacketSupperClass() {
		return AbstractRecvPacket.class;
	}

}
