package com.garow.net.socket.client.service;

import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.garow.net.socket.client.SimpleNioClientPool;

import com.garow.net.socket.client.config.ClientConfig;
import com.garow.net.web.service.InstanceService;
import com.garow.proto.PacketIds;
import com.garow.proto.transfer.KnockOuterClass.Knock;
import com.garow.proto.utils.ProtobufUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 连接服务
 * 
 * @author seg
 *
 */
@Service
public class ConnectService {
	private static Logger		LOG	= LoggerFactory.getLogger(ConnectService.class);
	@Resource
	private SimpleNioClientPool	simpleNioClientPool;
	@Resource
	private InstanceService		instanceService;

	/**
	 * 获取channel
	 * 
	 * @param host
	 * @return
	 */
	public Channel getChannel(String host) {
		ChannelFuture chf = simpleNioClientPool.getChannelFuture(host);
		if (chf == null)
			return null;
		return chf.channel();
	}

	/**
	 * 异步连接，成功后自动发送knock协议
	 * 
	 * @param config
	 * @return
	 */
	public ChannelFuture connect(ClientConfig config) {
		ChannelFuture chf = null;
		try {
			chf = simpleNioClientPool.connect(config, false);
			chf.addListener(new GenericFutureListener<ChannelFuture>() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						future.channel().writeAndFlush(ProtobufUtil.toByteArray(PacketIds.KNOCK,
								Knock.newBuilder().setClientHost(instanceService.getTcpHost()).build()));
					}
				}
			});
		} catch (IOException e) {
			LOG.error("connect error:", e);
		}
		return chf;
	}

}
