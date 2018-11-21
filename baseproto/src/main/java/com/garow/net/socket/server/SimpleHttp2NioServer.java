package com.garow.net.socket.server;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.garow.net.socket.server.config.ServerConfig;
import com.garow.net.socket.server.http2.Http2ServerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * http2服务器，目前完全不支持serverconfig的handler配置
 * https://medium.com/@chanakadkb/lets-make-server-push-enabled-http-2-server-with-netty-1e54134dc90b
 * 
 * @author seg
 *
 */
public class SimpleHttp2NioServer implements NioServer {
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

	private boolean				ssl;
	private SslContext			sslCtx;

	@PostConstruct
	public void init() {
		int cores = Runtime.getRuntime().availableProcessors();
		bossGroup = new NioEventLoopGroup(Math.max(1, cores / 2));
		workersGroup = new NioEventLoopGroup(Math.min(16, cores * 2));
		bzExecutorGroup = new DefaultEventExecutorGroup(Math.min(32, cores * 4));
		ssl = System.getProperty("ssl") != null;
		if (ssl) {
			SslProvider provider = OpenSsl.isAlpnSupported() ? SslProvider.OPENSSL : SslProvider.JDK;
			try {
				SelfSignedCertificate ssc = new SelfSignedCertificate();
				sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(provider)
						/*
						 * NOTE: the cipher filter may not include all ciphers required by the HTTP/2
						 * specification. Please refer to the HTTP/2 specification for cipher
						 * requirements.
						 */
						.ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
						.applicationProtocolConfig(new ApplicationProtocolConfig(Protocol.ALPN,
								// NO_ADVERTISE is currently the only mode supported by both OpenSsl and JDK
								// providers.
								SelectorFailureBehavior.NO_ADVERTISE,
								// ACCEPT is currently the only mode supported by both OpenSsl and JDK
								// providers.
								SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2,
								ApplicationProtocolNames.HTTP_1_1))
						.build();
			} catch (SSLException | CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			sslCtx = null;
		}
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
					.childHandler(new Http2ServerInitializer(sslCtx, bzExecutorGroup, config)).option(ChannelOption.SO_BACKLOG, config.getBacklog())
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
