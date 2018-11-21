package com.garow.net.socket.client.config;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.garow.net.web.utils.IpUtils;

import io.netty.channel.ChannelHandler;
/**
 * 客户端配置
 * @author seg
 *
 */
public class ClientConfig {
	/**远程目标主机*/
	private SocketAddress remoteAddr;
	/**编解码器*/
	private ChannelHandler codec;
	/**包解析器*/
	private ChannelHandler packetParser;
	/**业务处理*/
	private ChannelHandler bzHandler;
	/**本地端口，可选*/
	private SocketAddress localPort;

	public ChannelHandler getCodecHandler() {
		return codec;
	}

	public ChannelHandler getBzHandler() {
		return bzHandler;
	}

	public SocketAddress getLocalPort() {
		return localPort;
	}
	
	public void setLocalPort(int port) {
		this.localPort = new InetSocketAddress(port);
	}

	public SocketAddress getRemoteAddr() {
		return remoteAddr;
	}
	
	public void setRemoteAddr(String host) {
		this.remoteAddr = IpUtils.parseAddr(host);
	}

	public void setRemoteAddr(SocketAddress remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public ChannelHandler getPacketParser() {
		return packetParser;
	}

	public void setPacketParser(ChannelHandler packetParser) {
		this.packetParser = packetParser;
	}

	public void setCodec(ChannelHandler codec) {
		this.codec = codec;
	}

	public void setBzHandler(ChannelHandler bzHandler) {
		this.bzHandler = bzHandler;
	}

}
