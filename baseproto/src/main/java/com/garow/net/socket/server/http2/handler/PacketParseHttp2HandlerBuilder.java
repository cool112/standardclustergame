package com.garow.net.socket.server.http2.handler;

import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2Settings;
import static io.netty.handler.logging.LogLevel.INFO;

/**
 * 包解析handler构造器
 * 
 * @author seg
 *
 */
public class PacketParseHttp2HandlerBuilder
		extends AbstractHttp2ConnectionHandlerBuilder<PacketParseHttp2Handler, PacketParseHttp2HandlerBuilder> {
	private static final Http2FrameLogger logger = new Http2FrameLogger(INFO, PacketParseHttp2Handler.class);

	@Override
	protected PacketParseHttp2Handler build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder,
			Http2Settings initialSettings) throws Exception {
		PacketParseHttp2Handler handler = new PacketParseHttp2Handler(decoder, encoder, initialSettings);
		frameListener(handler);
		return handler;
	}

	public PacketParseHttp2HandlerBuilder() {
		frameLogger(logger);
	}

	@Override
	public PacketParseHttp2Handler build() {
		return super.build();
	}

}
