package com.garow.net.socket.server.http2.handler;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.buffer.Unpooled.unreleasableBuffer;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.CharsetUtil;

/**
 * http2包解析
 * 
 * @author seg
 *
 */
public class PacketParseHttp2Handler extends Http2ConnectionHandler implements Http2FrameListener {
	static final ByteBuf RESPONSE_BYTES = unreleasableBuffer(
			copiedBuffer("Only support http2 protobuf", CharsetUtil.UTF_8));

	protected PacketParseHttp2Handler(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder,
			Http2Settings initialSettings) {
		super(decoder, encoder, initialSettings);
	}

	private static Http2Headers http1HeadersToHttp2Headers(FullHttpRequest request) {
		CharSequence host = request.headers().get(HttpHeaderNames.HOST);
		Http2Headers http2Headers = new DefaultHttp2Headers().method(HttpMethod.GET.asciiName()).path(request.uri())
				.scheme(HttpScheme.HTTP.name());
		if (host != null) {
			http2Headers.authority(host);
		}
		return http2Headers;
	}

	/**
	 * Handles the cleartext HTTP upgrade event. If an upgrade occurred, sends a
	 * simple response via HTTP/2 on stream 1 (the stream specifically reserved for
	 * cleartext HTTP upgrade).
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof HttpServerUpgradeHandler.UpgradeEvent) {
			HttpServerUpgradeHandler.UpgradeEvent upgradeEvent = (HttpServerUpgradeHandler.UpgradeEvent) evt;
			onHeadersRead(ctx, 1, http1HeadersToHttp2Headers(upgradeEvent.upgradeRequest()), 0, true);
		}
		super.userEventTriggered(ctx, evt);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		ctx.close();
	}

	/**
	 * Sends a DATA frame to the client.
	 */
	private void sendResponse(ChannelHandlerContext ctx, int streamId, ByteBuf payload) {
		// Send a frame for the response status
		Http2Headers headers = new DefaultHttp2Headers().status(OK.codeAsText());
		encoder().writeHeaders(ctx, streamId, headers, 0, false, ctx.newPromise());
		encoder().writeData(ctx, streamId, payload, 0, true, ctx.newPromise());

		// no need to call flush as channelReadComplete(...) will take care of it.
	}

	@Override
	public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream)
			throws Http2Exception {
		int processed = data.readableBytes() + padding;
		//需要考虑把encoder传递到业务处理逻辑中，不可通过ctx.channel().write()回写数据
		ctx.fireChannelRead(data.retain());
		if (endOfStream) {
			sendResponse(ctx, streamId, data.retain());
		}
		return processed;
	}

	@Override
	public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding,
			boolean endOfStream) throws Http2Exception {
		if (endOfStream) {
			ByteBuf content = ctx.alloc().buffer();
			content.writeBytes(RESPONSE_BYTES.duplicate());
			ByteBufUtil.writeAscii(content, " - via HTTP/2");
			sendResponse(ctx, streamId, content);
		}

	}

	@Override
	public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency,
			short weight, boolean exclusive, int padding, boolean endOfStream) throws Http2Exception {
		onHeadersRead(ctx, streamId, headers, padding, endOfStream);
	}

	@Override
	public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight,
			boolean exclusive) throws Http2Exception {

	}

	@Override
	public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {

	}

	@Override
	public void onSettingsAckRead(ChannelHandlerContext ctx) throws Http2Exception {

	}

	@Override
	public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception {

	}

	@Override
	public void onPingRead(ChannelHandlerContext ctx, long data) throws Http2Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPingAckRead(ChannelHandlerContext ctx, long data) throws Http2Exception {

	}

	@Override
	public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers,
			int padding) throws Http2Exception {

	}

	@Override
	public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData)
			throws Http2Exception {

	}

	@Override
	public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement)
			throws Http2Exception {

	}

	@Override
	public void onUnknownFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags,
			ByteBuf payload) throws Http2Exception {

	}

}
