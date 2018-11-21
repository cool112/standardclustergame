package com.garow.net.socket.server.handler;

import com.alibaba.fastjson.JSON;
import com.garow.base.constants.ErrorStatus;
import com.garow.base.view.ErrorCode;
import com.garow.net.socket.server.service.WebSocketService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

/**
 * websocket处理
 * 
 * @author seg
 *
 */
@Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
		if (msg instanceof CloseWebSocketFrame) {
			WebSocketServerHandshaker handshaker = WebSocketService.getWebSocketHandshakerMap()
					.get(ctx.channel().id().asLongText());
			if (handshaker == null) {
				sendErrorMessage(ctx, "不存在的客户端连接！");
			} else {
				handshaker.close(ctx.channel(), (CloseWebSocketFrame) msg.retain());
			}
			return;
		}

		// ping请求
		if (msg instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(msg.content().retain()));
			return;
		}

		if (msg instanceof TextWebSocketFrame) {
			sendErrorMessage(ctx, "仅支持protobuf格式，不支持text消息");
			return;
		} else if (msg instanceof BinaryWebSocketFrame) {
			BinaryWebSocketFrame byteFrame = (BinaryWebSocketFrame) msg;
			ctx.fireChannelRead(byteFrame.retain().content());//交给包解析handler
		}

	}

	private void sendErrorMessage(ChannelHandlerContext ctx, String errorMsg) {
		ErrorCode errorCode = new ErrorCode(ErrorStatus.INVALID_SESSION, errorMsg);
		ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errorCode)));
	}

	/**
	 * 异常处理：关闭channel
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
