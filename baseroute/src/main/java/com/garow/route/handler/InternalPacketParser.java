package com.garow.route.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.garow.net.socket.packet.AbstractRecvPacket;
import com.garow.route.packet.InternalPacketFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * 内部包解析器
 * 
 * @author seg
 *
 */
@Sharable
public class InternalPacketParser extends SimpleChannelInboundHandler<ByteBuf> {
	private static final Logger		LOG	= LoggerFactory.getLogger(InternalPacketParser.class);
	@Autowired
	private InternalPacketFactory	internalPacketFactory;

	@SuppressWarnings("rawtypes")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		short pid = msg.readShortLE();
		LOG.info("internal packet:" + pid);
		AbstractRecvPacket newPacket = internalPacketFactory.newRecvPacket(pid);
		if (newPacket == null) {
			LOG.error("unknown internal pid:" + pid);
			return;
		}
		newPacket.setCtx(ctx);
		newPacket.readParams(msg);
		ctx.fireChannelRead(newPacket);
	}

}
