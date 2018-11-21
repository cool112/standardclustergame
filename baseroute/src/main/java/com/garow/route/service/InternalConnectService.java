package com.garow.route.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.garow.net.socket.client.config.ClientConfig;
import com.garow.net.socket.client.service.ConnectService;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

/**
 * 内部连接服务
 * 
 * @author seg
 *
 */
@Service
public class InternalConnectService {
	@Resource
	private ConnectService	connectService;
	@Resource
	private ChannelHandler	innerCodec;
	@Resource
	private ChannelHandler	innerPacketParser;
	@Resource
	private ChannelHandler	innerBzHandler;

	public ChannelFuture connect(String tcpHost) {
		ClientConfig config = new ClientConfig();
		config.setRemoteAddr(tcpHost);
		config.setPacketParser(innerPacketParser);
		config.setCodec(innerCodec);
		config.setBzHandler(innerBzHandler);
		return connectService.connect(config);
	}
}
