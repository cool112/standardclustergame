package com.garow.tcpgateway.server.handler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.garow.base.constants.ErrorStatus;
import com.garow.net.socket.client.service.ProtoClientServiceContext;
import com.garow.net.socket.constants.ContextKeys;
import com.garow.net.socket.packet.AbstractRecvPacket;
import com.garow.proto.PacketIds;
import com.garow.proto.common.AckOuterClass.Ack;
import com.garow.proto.conn.Offline.OfflineEvent;
import com.garow.proto.utils.ProtobufUtil;
import com.garow.route.cache.RouteManager;
import com.garow.route.config.RouteConfigContext;
import com.garow.route.service.RouteServiceContext;
import com.garow.route.utils.InternalPacketUtil;
import com.garow.tcpgateway.server.packet.ClientPacketFactory;
import com.garow.tcpgateway.server.service.TcpGatewayServiceContext;
import com.garow.tcpgateway.server.task.NoAuthTimeoutTask;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 业务解析类,从pid开始
 * 
 * @author gjs
 */
@Sharable
public class PacketParseHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private static final Logger	LOG					= LoggerFactory.getLogger(PacketParseHandler.class);
	/** 校验协议 */
	private static final short	AUTH_PROTOCOL_ID	= 1001;
	@Autowired
	private ClientPacketFactory	ClientPacketFactory;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().attr(ContextKeys.KEY_AUTH).compareAndSet(null, false);
		ctx.channel().attr(ContextKeys.KEY_LAST_LIVE).compareAndSet(null, System.currentTimeMillis());
		LOG.info("client connect remote:" + ctx.channel().remoteAddress());
		ctx.executor().schedule(new NoAuthTimeoutTask(ctx), 10, TimeUnit.SECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.
	 * ChannelHandlerContext, java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		short pid = msg.readShortLE();
		Boolean authState = ctx.channel().attr(ContextKeys.KEY_AUTH).get();
		if (authState == null || !authState) {
			if (pid != AUTH_PROTOCOL_ID) {
				LOG.error("unauthentic proto:" + pid);
				ctx.channel().write(ProtobufUtil.toByteArray(PacketIds.ACK,
						Ack.newBuilder().setErrCode(ErrorStatus.NO_AUTH).setProId(pid).build()));
				return;
			} else {
				// 如果是鉴权协议可以允许续租无session超时时间,暂定
				ctx.channel().attr(ContextKeys.KEY_LAST_LIVE).set(System.currentTimeMillis());
			}
		}
		String sessionId = ctx.channel().attr(ContextKeys.KEY_SESSION).get();
		AbstractRecvPacket packet = parsePacket(pid, sessionId);
		if (packet == null)
			return;

		packet.readParams(msg);
		packet.setCtx(ctx);
		ctx.fireChannelRead(packet);
	}

	/**
	 * 解析业务包，大概率会根据pid转发到不同app，所以提供session获取路由和封包
	 * 
	 * @param pid
	 * @param session
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private AbstractRecvPacket parsePacket(short pid, String sessionId) {
		AbstractRecvPacket packet = null;
		packet = ClientPacketFactory.newRecvPacket(pid);
		return packet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.
	 * channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOG.error("channel exception", cause);
		ctx.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(io.netty.
	 * channel.ChannelHandlerContext)
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOG.info("channel closing remoteAddr:" + ctx.channel().remoteAddress());
		String sessionId = ctx.channel().attr(ContextKeys.KEY_SESSION).get();
		if (sessionId == null)
			return;
		TcpGatewayServiceContext.getConnectEventService().fireOffline(sessionId);
	}
}
