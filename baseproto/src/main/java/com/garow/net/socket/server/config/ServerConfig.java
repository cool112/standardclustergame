package com.garow.net.socket.server.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.netty.channel.ChannelHandler;

/**
 * netty配置，服务器handler需要主动声明
 * 
 * @author seg
 *
 */
@Configuration
public class ServerConfig {
	/** 监听端口 */
	@Value("${netty.server.listen:0}")
	private int				listen;
	/**握手等待队列*/
	@Value("${netty.server.backlog:10}")
	private int				backlog;
	/**端口重用*/
	@Value("${netty.server.reuse:true}")
	private boolean			reuse;
	/**链接会话超时时间，毫秒*/
	@Value("${netty.server.sessionTimeout:1800000}")
	private long			sessionTimeout;
	/**内网ip前缀*/
	@Value("${netty.server.loNetPrefix:192.168}")
	private String			loNetPrefix;
	/**
	 * 外网域名
	 */
	@Value("${netty.server.hostname:localhost}")
	private String hostname;
	/**监听acceptor handler*/
	@Resource
	private ChannelHandler	nioServerHandler;
	/**编解码器*/
	@Resource
	private ChannelHandler	codec;
	/**包解析器*/
	@Resource
	private ChannelHandler	packetParser;
	/**业务处理*/
	@Resource
	private ChannelHandler	bzHandler;

	public int getListen() {
		return listen;
	}

	public void setListen(int listen) {
		this.listen = listen;
	}

	public ChannelHandler getServerHandler() {
		return nioServerHandler;
	}

	public ChannelHandler getCodecHandler() {
		return codec;
	}

	public ChannelHandler getParsePacketHandler() {
		return packetParser;
	}

	public ChannelHandler getBzHandler() {
		return bzHandler;
	}

	public int getBacklog() {
		return backlog;
	}

	public boolean getReuseAddress() {
		return reuse;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public boolean isReuse() {
		return reuse;
	}

	public void setReuse(boolean reuse) {
		this.reuse = reuse;
	}

	public ChannelHandler getNioServerHandler() {
		return nioServerHandler;
	}

	public void setNioServerHandler(ChannelHandler nioServerHandler) {
		this.nioServerHandler = nioServerHandler;
	}

	public ChannelHandler getCodec() {
		return codec;
	}

	public void setCodec(ChannelHandler codec) {
		this.codec = codec;
	}

	public ChannelHandler getPacketParser() {
		return packetParser;
	}

	public void setPacketParser(ChannelHandler packetParser) {
		this.packetParser = packetParser;
	}

	public void setBzHandler(ChannelHandler bzHandler) {
		this.bzHandler = bzHandler;
	}

	public long getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public String getLoNetPrefix() {
		return loNetPrefix;
	}

	public void setLoNetPrefix(String loNetPrefix) {
		this.loNetPrefix = loNetPrefix;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

}
