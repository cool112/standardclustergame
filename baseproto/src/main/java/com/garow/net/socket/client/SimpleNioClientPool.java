package com.garow.net.socket.client;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.garow.net.socket.client.config.ClientConfig;
import com.garow.net.socket.constants.ContextKeys;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 简单nio客户端公共线程池
 * 
 * @author seg
 *
 */
@Component
public class SimpleNioClientPool implements NioClientPool {
	private static final Logger				LOG			= LoggerFactory.getLogger(SimpleNioClientPool.class);
	/**
	 * i/o task schedule pool
	 */
	protected EventLoopGroup				workersGroup;
	/** bz task pool */
	protected EventExecutorGroup			bzExecutorGroup;
	/** channelMap.remotehost=channelfuture */
	protected Map<String, ChannelFuture>	channelMap	= new ConcurrentHashMap<String, ChannelFuture>();

	/**
	 * 初始化线程池
	 */
	@PostConstruct
	protected void init() {
		int cores = Runtime.getRuntime().availableProcessors();
		workersGroup = new NioEventLoopGroup(Math.min(4, cores * 2));
		bzExecutorGroup = new DefaultEventExecutorGroup(Math.min(8, cores * 4));
	}

	public ChannelFuture connect(final ClientConfig config) throws IOException {
		return connect(config, true);
	}

	public ChannelFuture connect(final ClientConfig config, boolean isSync) throws IOException {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workersGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				if (config.getCodecHandler() != null)
					ch.pipeline().addLast("codec", config.getCodecHandler());
				if (config.getPacketParser() != null)
					ch.pipeline().addLast("packetParser", config.getPacketParser());
				if (config.getBzHandler() != null)
					ch.pipeline().addLast(bzExecutorGroup, "bzExecutor", config.getBzHandler());
			}
		});
		bootstrap.option(ChannelOption.TCP_NODELAY, true)
				// .option(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP,
				// true)//没有同步要求的可以不用
				.option(ChannelOption.SO_KEEPALIVE, true);

		ChannelFuture channelFuture = null;
		try {
			if (config.getLocalPort() != null)
				channelFuture = bootstrap.connect(config.getRemoteAddr(), config.getLocalPort());
			else
				channelFuture = bootstrap.connect(config.getRemoteAddr());

			if (channelFuture != null) {
				channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {

					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if (future.isSuccess()) {
							channelMap.put(config.getRemoteAddr().toString(), future);
							future.channel().attr(ContextKeys.KEY_SESSION).compareAndSet(null,
									config.getRemoteAddr().toString());
							LOG.info("server connect remoteAddr:" + config.getRemoteAddr());
						} else
							LOG.error("server connect fail! remoteAddr:" + config.getRemoteAddr());
					}
				});
			}
			if (isSync)
				channelFuture.sync();

		} catch (InterruptedException e) {
			LOG.error("client connect fail!", e);
		}
		return channelFuture;
	}

	public ChannelFuture getChannelFuture(String tcpHost) {
		return channelMap.get(tcpHost);
	}
}
