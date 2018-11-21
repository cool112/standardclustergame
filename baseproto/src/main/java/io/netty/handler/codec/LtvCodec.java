package io.netty.handler.codec;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
/**
 * ltv编解码器，格式
 * len pid body
 * 2   2   arbitrar
 * @author seg
 *
 */
@Sharable
public class LtvCodec extends MessageToMessageCodec<ByteBuf, Object> {
	private static final Logger LOG = LoggerFactory.getLogger(LtvCodec.class);
	private ByteToMessageDecoder decoder = new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 1024*1024, 0, 2, 0, 2, false);
	private MessageToMessageEncoder<ByteBuf> encoder = new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 2, 0, false);
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		if (msg instanceof byte[]) {
			byte[] bytes = (byte[]) msg;
//			LOG.info("send: " + Arrays.toString(bytes));
			ByteBuf buffer = ctx.alloc().buffer(bytes.length);
			buffer.writeBytes(bytes);
			encoder.encode(ctx, buffer, out);
		}
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
//		LOG.info("recv: "+msg.readableBytes());
		//目前只有分包，可补充粘包机制
		while(msg.readableBytes() > 2) {
			decoder.decode(ctx, msg, out);
		}
	}

}
