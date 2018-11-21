package com.garow.tcpgateway.server.service;

import org.springframework.stereotype.Service;

import com.garow.net.socket.client.service.ProtoClientServiceContext;
import com.garow.proto.PacketIds;
import com.garow.proto.conn.Offline.OfflineEvent;
import com.garow.proto.conn.Online.OnlineEvent;
import com.garow.proto.utils.ProtobufUtil;
import com.garow.route.cache.RouteManager;
import com.garow.route.config.RouteConfigContext;
import com.garow.route.service.RouteServiceContext;
import com.garow.route.utils.InternalPacketUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
/**
 * 连接事件服务
 * @author seg
 *
 */
@Service
public class ConnectEventService {
	/**
	 * 广播离线事件
	 * @param sessionId
	 */
	public void fireOffline(String sessionId) {
		OfflineEvent packet = OfflineEvent.newBuilder().setType(0).build();
		byte[] data = ProtobufUtil.toByteArray(PacketIds.OFFLINE, packet);
		broadcast(sessionId, data);
	}
	/**
	 * 广播在线事件
	 * @param sessionId
	 */
	public void fireOnline(String sessionId) {
		OnlineEvent packet = OnlineEvent.newBuilder().setType(0).build();
		byte[] data = ProtobufUtil.toByteArray(PacketIds.ONLINE, packet);
		broadcast(sessionId, data);
	}
	/**
	 * 广播，需要用户会话先行绑定各个tcp应用路由
	 * @param sessionId
	 * @param data
	 */
	private void broadcast(String sessionId, byte[] data) {
		for(String app:RouteConfigContext.getRouteConfig().getApps()) {
			if(app.equals(InternalPacketUtil.GATEWAY_APP))
				continue;
			String host = RouteManager.getRoute(sessionId, app);
			if(host != null) {
				Channel ch = ProtoClientServiceContext.getConnectService().getChannel(host);
				if(ch == null || !ch.isWritable()) {
					ChannelFuture chf = RouteServiceContext.getInternalConnectService().connect(host);
					chf.addListener(new GenericFutureListener<ChannelFuture>() {

						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							if(future.isSuccess())
								InternalPacketUtil.clientSend(host, sessionId, data);
						}
					});
				}
				else
					InternalPacketUtil.clientSend(host, sessionId, data);
			}
		}
	}
}
