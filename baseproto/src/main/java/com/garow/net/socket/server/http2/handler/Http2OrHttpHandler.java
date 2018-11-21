package com.garow.net.socket.server.http2.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;

/**
 * Negotiates with the browser if HTTP2 or HTTP is going to be used. Once
 * decided, the Netty pipeline is setup with the correct handlers for the
 * selected protocol. 使用tls时进行的协议选择引导操作
 * 
 * @author seg
 *
 */
public class Http2OrHttpHandler extends ApplicationProtocolNegotiationHandler {
	private static final int MAX_CONTENT_LENGTH = 1024 * 100;

	public Http2OrHttpHandler() {
		super(ApplicationProtocolNames.HTTP_1_1);
	}

	@Override
	protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
		if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
			ctx.pipeline().addLast(new PacketParseHttp2HandlerBuilder().build());
			return;
		}

		if (ApplicationProtocolNames.HTTP_1_1.equals(protocol)) {
			ctx.pipeline().addLast(new HttpServerCodec(), new HttpObjectAggregator(MAX_CONTENT_LENGTH),
					new PacketParseHttp1Handler("ALPN Negotiation"));
			return;
		}

		throw new IllegalStateException("unknown protocol: " + protocol);
	}

}
