/**
 * 
 */
package com.garow.net.socket.server.handler;

import com.garow.net.socket.packet.AbstractRecvPacket;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 执行包业务
 * @author gjs
 * 2018年7月26日
 */
@Sharable
public class PacketExecuteHandler extends
		SimpleChannelInboundHandler<AbstractRecvPacket> {

	/* (non-Javadoc)
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,
			AbstractRecvPacket msg) throws Exception {
		if(msg == null)
			return;
		
		msg.run();
	}

}
