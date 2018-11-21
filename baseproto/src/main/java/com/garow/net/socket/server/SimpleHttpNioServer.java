package com.garow.net.socket.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.garow.net.socket.server.config.ServerConfig;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 简单httpserver，同时支持http和websocket
 * 
 * @author seg
 *
 */
public class SimpleHttpNioServer implements NioServer {
	private static final Logger	LOG					= LoggerFactory.getLogger(SimpleHttpNioServer.class);
	/**
	 * acceptor pool
	 */
	private EventLoopGroup		bossGroup;
	/**
	 * i/o task schedule pool
	 */
	private EventLoopGroup		workersGroup;

	/**
	 * listen channel promise
	 */
	private Set<ChannelFuture>	channelFutureSet	= new HashSet<ChannelFuture>();
	private EventExecutorGroup	bzExecutorGroup;

	@PostConstruct
	public void init() {
		int cores = Runtime.getRuntime().availableProcessors();
		bossGroup = new NioEventLoopGroup(Math.max(1, cores / 2));
		workersGroup = new NioEventLoopGroup(Math.min(16, cores * 2));
		bzExecutorGroup = new DefaultEventExecutorGroup(Math.min(32, cores * 4));
	}

	/**
	 * 关闭服务器方法
	 */
	@PreDestroy
	public void close() {
		LOG.info("关闭服务器....");
		try {
			for (ChannelFuture channelFuture : channelFutureSet) {
				channelFuture.channel().close().sync();
			}
		} catch (InterruptedException e) {
			LOG.error("server close fail!", e);
		} finally {
			// 优雅退出
			bossGroup.shutdownGracefully();
			workersGroup.shutdownGracefully();
			bzExecutorGroup.shutdownGracefully();
		}
	}

	@Override
	public void start(ServerConfig config) throws IOException {
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workersGroup).channel(NioServerSocketChannel.class)
					.handler(config.getServerHandler() == null ? new LoggingHandler("nioserver", LogLevel.INFO)
							: config.getServerHandler())
					.childHandler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {
							ch.pipeline().addLast("http-codec", new HttpServerCodec()); // HTTP编码解码器
							ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536)); // 把HTTP头、HTTP体拼成完整的HTTP请求
							ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
							//不使用自身的codec
//							if (config.getCodecHandler() != null)
//								ch.pipeline().addLast("codec", config.getCodecHandler());
							if (config.getParsePacketHandler() != null)
								ch.pipeline().addLast("packetParse", config.getParsePacketHandler());
							if (config.getBzHandler() != null)
								ch.pipeline().addLast(bzExecutorGroup, "bzHandler", config.getBzHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, config.getBacklog())
					.option(ChannelOption.SO_REUSEADDR, config.getReuseAddress())
					.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, true);

			ChannelFuture channelFuture = bootstrap.bind(config.getListen()).sync();
			channelFutureSet.add(channelFuture);
		} catch (InterruptedException e) {
			LOG.error("server start fail!", e);
		}

	}

}
