package com.garow.tcpgateway.proto.transfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.garow.base.constants.ClientCmd;
import com.garow.net.socket.annotation.BzPacket;
import com.garow.net.socket.constants.PacketType;
import com.garow.net.socket.packet.AbstractRecvPacket;
import com.garow.proto.PacketIds;
import com.garow.proto.transfer.Toclient.ToClient;
import com.garow.tcpgateway.cache.ClientConnectManager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 客户端转发包，目前实现了3个命令
 * @author seg
 *
 */
@BzPacket(id = PacketIds.TO_CLIENT, type = PacketType.INTERNAL)
public class ToClientRecvPacket extends AbstractRecvPacket<ToClient> {
	private static final Logger	LOG					= LoggerFactory.getLogger(ToClientRecvPacket.class);

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5787868172679660552L;

	@Override
	protected void readParamsImpl(byte[] dst) throws Exception {
		data = ToClient.parseFrom(dst);
	}

	@Override
	public int getPacketId() {
		return PacketIds.TO_CLIENT;
	}

	@Override
	protected void runImpl() {
		int cmd = data.getCmd();
		byte[] packet = null;
		switch (cmd) {
		case ClientCmd.TRANSFER:
			if (data.getSessionIdsCount() <= 0)
				break;
			packet = data.getData().toByteArray();
			for (String sessionId : data.getSessionIdsList()) {
				LOG.info("send to {}, cmd:{}", sessionId, cmd);
				Channel channel = ClientConnectManager.getChannel(sessionId);
				if (channel != null && channel.isWritable())
					channel.writeAndFlush(packet);
			}
			break;
		case ClientCmd.CLOSE:
			if (data.getSessionIdsCount() <= 0)
				break;
			for (String sessionId : data.getSessionIdsList()) {
				Channel channel = ClientConnectManager.getChannel(sessionId);
				if (channel != null && channel.isWritable())
					channel.close();
			}
			break;
		case ClientCmd.TRANSFER_AND_CLOSE:
			if (data.getSessionIdsCount() <= 0)
				break;
			packet = data.getData().toByteArray();
			for (String sessionId : data.getSessionIdsList()) {
				Channel channel = ClientConnectManager.getChannel(sessionId);
				if (channel != null && channel.isWritable()) {
					ChannelFuture chf = channel.write(packet);
					chf.addListener(new GenericFutureListener<ChannelFuture>() {

						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							if (future.isSuccess())
								future.channel().close();
						}
					});
				}
			}
			break;
		default:
			break;
		}
	}

}
