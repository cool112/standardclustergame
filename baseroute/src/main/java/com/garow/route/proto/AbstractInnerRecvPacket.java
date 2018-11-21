package com.garow.route.proto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.garow.net.socket.annotation.BzPacket;
import com.garow.net.socket.constants.PacketType;
import com.garow.net.socket.packet.AbstractPacketFactory;
import com.garow.net.socket.packet.AbstractRecvPacket;
import com.garow.proto.PacketIds;
import com.garow.proto.transfer.Toserver.ToServer;
import com.garow.route.packet.AbstractSessionRecvPacket;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * 抽象内部转发包,需要实现自己的包工厂
 * 
 * @author seg
 *
 */
@BzPacket(id = PacketIds.TO_SERVER, type = PacketType.INTERNAL)
public abstract class AbstractInnerRecvPacket extends AbstractRecvPacket<ToServer> {
	private static final Logger			LOG					= LoggerFactory.getLogger(AbstractInnerRecvPacket.class);
	/**
	 * 
	 */
	private static final long			serialVersionUID	= 7174439707599987997L;
	private FastThreadLocal<ByteBuffer>	buffer				= new FastThreadLocal<ByteBuffer>() {
																@Override
																protected ByteBuffer initialValue() throws Exception {
																	ByteBuffer bb = ByteBuffer.allocate(2);
																	bb.order(ByteOrder.LITTLE_ENDIAN);
																	return bb;
																}
															};

	@Override
	public int getPacketId() {
		return PacketIds.TO_SERVER;
	}

	@Override
	protected void readParamsImpl(byte[] dst) throws Exception {
		data = ToServer.parseFrom(dst);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void runImpl() {
		String sessionId = data.getSessionId();
		ByteString subPacketBuf = data.getData();
		if (subPacketBuf.size() < 2) {
			LOG.error("inner recv packet length less than 2");
			return;
		}
		byte[] pidArr = subPacketBuf.substring(0, 2).toByteArray();
		int packetId = getPid(pidArr);
		AbstractSessionRecvPacket subPacket = getPacketFactory().newRecvPacket(packetId);
		if (subPacket == null) {
			return;
		}
		subPacket.readParams(subPacketBuf.substring(2));
		subPacket.setCtx(getCtx());
		subPacket.setSessionId(sessionId);
		subPacket.run();
	}

	private int getPid(byte[] pidArr) {
		ByteBuffer bb = buffer.get();
		bb.put(pidArr[0]);
		bb.put(pidArr[1]);
		int packetId = bb.getShort(0);
		bb.clear();
		return packetId;
	}

	@SuppressWarnings("rawtypes")
	protected abstract AbstractPacketFactory<AbstractSessionRecvPacket> getPacketFactory();

}
