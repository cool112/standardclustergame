package com.garow.route.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.data.mongo.MongoSession;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;
import com.garow.auth.service.AuthServiceContext;
import com.garow.base.constants.ClientCmd;
import com.garow.base.view.InstanceInfo;
import com.garow.net.socket.client.service.ProtoClientServiceContext;
import com.garow.proto.PacketIds;
import com.garow.proto.transfer.Toclient.ToClient;
import com.garow.proto.transfer.Toserver.ToServer;
import com.garow.proto.utils.ProtobufUtil;
import com.garow.route.cache.InternalConnectManager;
import com.garow.route.cache.RouteManager;

import io.netty.channel.Channel;

/**
 * 内部发包工具
 * 
 * @author seg
 *
 */
public class InternalPacketUtil {
	private static final Logger LOG = LoggerFactory.getLogger(InternalPacketUtil.class);
	public static final String GATEWAY_APP = "tcpgateway";
	/**
	 * 作为服务器发送给客户端
	 * @param host
	 * @param sessionId
	 * @param message
	 */
	public static void serverSend(String host, String sessionId, byte[] message) {
		Channel channel = InternalConnectManager.getChannel(host);
		if (channel == null || !channel.isWritable())
			return;
		ByteString data = ByteString.copyFrom(message);
		ToServer packet = ToServer.newBuilder().setSessionId(sessionId).setData(data).build();
		channel.writeAndFlush(ProtobufUtil.toByteArray(PacketIds.TO_SERVER, packet));
	}
	/**
	 * 作为客户端发送给服务器
	 * @param host
	 * @param sessionId
	 * @param message
	 */
	public static void clientSend(String host, String sessionId, byte[] message) {
		Channel channel = ProtoClientServiceContext.getConnectService().getChannel(host);
		if (channel == null || !channel.isWritable())
			return;
		ByteString data = ByteString.copyFrom(message);
		ToServer packet = ToServer.newBuilder().setSessionId(sessionId).setData(data).build();
		channel.writeAndFlush(ProtobufUtil.toByteArray(PacketIds.TO_SERVER, packet));
	}
	/**
	 * 发送给网关的用户客户端
	 * @param sessionId
	 * @param message
	 */
	public static void sendGateway(String sessionId, byte[] message) {
		String host = RouteManager.getRoute(sessionId, GATEWAY_APP);
		if(host == null) {
			MongoSession session = AuthServiceContext.getMongoOperationsSessionRepository().findById(sessionId);
			if (session == null)
				return;
			Object gatewayInfo = session.getAttribute(GATEWAY_APP);
			if (gatewayInfo == null)
				return;
			host = ((InstanceInfo) gatewayInfo).getTcpHost();
		}
		sendClient(host, sessionId, message);
	}
	/**
	 * 发送用户客户端包
	 * @param host
	 * @param sessionId
	 * @param message
	 */
	public static void sendClient(String host, String sessionId, byte[] message) {
		Channel channel = InternalConnectManager.getChannel(host);
		if (channel == null || !channel.isWritable())
			return;
		ByteString data = ByteString.copyFrom(message);
		ToClient packet = ToClient.newBuilder().setCmd(ClientCmd.TRANSFER).addSessionIds(sessionId).setData(data)
				.build();
		LOG.info("send to client:" + sessionId);
		channel.writeAndFlush(ProtobufUtil.toByteArray(PacketIds.TO_CLIENT, packet));
	}
}
