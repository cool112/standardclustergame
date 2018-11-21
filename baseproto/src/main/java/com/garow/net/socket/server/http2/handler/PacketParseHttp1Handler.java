package com.garow.net.socket.server.http2.handler;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.util.internal.ObjectUtil.checkNotNull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpUtil;
/**
 * 如果是http1，暂时不处理，返回拒绝信息
 * @author seg
 *
 */
public class PacketParseHttp1Handler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private final String establishApproach;

    public PacketParseHttp1Handler(String establishApproach) {
        this.establishApproach = checkNotNull(establishApproach, "establishApproach");
    }
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		 if (HttpUtil.is100ContinueExpected(req)) {
	            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
	        }
	        boolean keepAlive = HttpUtil.isKeepAlive(req);

	        ByteBuf content = ctx.alloc().buffer();
	        content.writeBytes(PacketParseHttp2Handler.RESPONSE_BYTES.duplicate());
	        ByteBufUtil.writeAscii(content, " - via " + req.protocolVersion() + " (" + establishApproach + ")");

	        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
	        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
	        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

	        if (!keepAlive) {
	            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
	        } else {
	            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
	            ctx.write(response);
	        }
		
	}

}
