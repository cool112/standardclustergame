package com.garow.net.socket.server.http2;

import com.garow.net.socket.server.http2.handler.PacketParseHttp1Handler;
import com.garow.net.socket.server.http2.handler.PacketParseHttp2HandlerBuilder;
import com.garow.net.socket.server.config.ServerConfig;
import com.garow.net.socket.server.http2.handler.Http2OrHttpHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodecFactory;
import io.netty.handler.codec.http2.CleartextHttp2ServerUpgradeHandler;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2ServerUpgradeCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * http2channel初始化器
 * 
 * @author seg
 *
 */
public class Http2ServerInitializer extends ChannelInitializer<SocketChannel> {
	private static final UpgradeCodecFactory	upgradeCodecFactory	= new UpgradeCodecFactory() {
																		@Override
																		public UpgradeCodec newUpgradeCodec(
																				CharSequence protocol) {
																			if (AsciiString.contentEquals(
																					Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME,
																					protocol)) {
																				return new Http2ServerUpgradeCodec(
																						new PacketParseHttp2HandlerBuilder()
																								.build());
																			} else {
																				return null;
																			}
																		}
																	};
	private final SslContext					sslCtx;
	private final int							maxHttpContentLength;
	private EventExecutorGroup bzGroup;
	private ServerConfig config;

	public Http2ServerInitializer(SslContext sslCtx) {
		this(sslCtx, 16 * 1024, null, null);
	}
	
	public Http2ServerInitializer(SslContext sslCtx, EventExecutorGroup bzGroup, ServerConfig config) {
		this(sslCtx, 16 * 1024, bzGroup,config);
	}

	public Http2ServerInitializer(SslContext sslCtx, int maxHttpContentLength, EventExecutorGroup bzGroup, ServerConfig config) {
		if (maxHttpContentLength < 0) {
			throw new IllegalArgumentException("maxHttpContentLength (expected >= 0): " + maxHttpContentLength);
		}
		this.sslCtx = sslCtx;
		this.maxHttpContentLength = maxHttpContentLength;
		this.bzGroup = bzGroup;
		this.config = config;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		 if (sslCtx != null) {
	            configureSsl(ch);
	        } else {
	            configureClearText(ch);
	        }

	}
	 /**
     * Configure the pipeline for TLS NPN negotiation to HTTP/2.
     */
    private void configureSsl(SocketChannel ch) {
        ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()), new Http2OrHttpHandler());
        ch.pipeline().addLast(bzGroup, config.getBzHandler());
    }

    /**
     * Configure the pipeline for a cleartext upgrade from HTTP to HTTP/2.0
     */
    private void configureClearText(SocketChannel ch) {
        final ChannelPipeline p = ch.pipeline();
        final HttpServerCodec sourceCodec = new HttpServerCodec();
        final HttpServerUpgradeHandler upgradeHandler = new HttpServerUpgradeHandler(sourceCodec, upgradeCodecFactory);
        final CleartextHttp2ServerUpgradeHandler cleartextHttp2ServerUpgradeHandler =
                new CleartextHttp2ServerUpgradeHandler(sourceCodec, upgradeHandler,
                                                       new PacketParseHttp2HandlerBuilder().build());

        p.addLast(cleartextHttp2ServerUpgradeHandler);
        p.addLast(new SimpleChannelInboundHandler<HttpMessage>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
                // If this handler is hit then no upgrade has been attempted and the client is just talking HTTP.
                System.err.println("Directly talking: " + msg.protocolVersion() + " (no upgrade was attempted)");
                ChannelPipeline pipeline = ctx.pipeline();
                ChannelHandlerContext thisCtx = pipeline.context(this);
                pipeline.addAfter(thisCtx.name(), null, new PacketParseHttp1Handler("Direct. No Upgrade Attempted."));
                pipeline.replace(this, null, new HttpObjectAggregator(maxHttpContentLength));
                ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
            }
        });
        p.addLast(bzGroup, config.getBzHandler());

    }
}
