package com.garow.notifyserver.proto.transfer;

import org.springframework.stereotype.Component;

import com.garow.net.socket.packet.AbstractPacketFactory;
import com.garow.notifyserver.service.NotifyServiceContext;
import com.garow.route.packet.AbstractSessionRecvPacket;
import com.garow.route.proto.AbstractInnerRecvPacket;
/**
 * 推送服的内部转发包接收
 * @author seg
 *
 */
@Component
public class NotifyInternalRecvPacket extends AbstractInnerRecvPacket {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1349508648780418670L;

	@SuppressWarnings("rawtypes")
	@Override
	protected AbstractPacketFactory<AbstractSessionRecvPacket> getPacketFactory() {
		return NotifyServiceContext.getNotifyRecvPacketFactory();
	}


}
