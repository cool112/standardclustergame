/**
 * 
 */
package com.garow.net.socket.packet;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;


/**
 * 抽象接收包
 * @author gjs
 * 2018年7月25日
 */
public abstract class AbstractRecvPacket<T extends GeneratedMessageV3> implements Runnable, Serializable, Cloneable {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractRecvPacket.class);
	private static final long serialVersionUID = -175432892186698907L;
	private ChannelHandlerContext ctx;
	protected T data;
	/**
	 * 读取参数
	 * @param byteBuf
	 */
	public void readParams(ByteBuf byteBuf){
		int len = byteBuf.readableBytes();
		byte[] dst = new byte[len];
		byteBuf.readBytes(dst);
		try {
			readParamsImpl(dst);
		} catch (Throwable e) {
			LOG.error("read packet fail", e);
		}
		finally{
		}
	}
	/**
	 * 读取参数，protobuf嵌套包
	 * @param byteString
	 */
	public void readParams(ByteString byteString) {
		byte[] dst = byteString.toByteArray();
		try {
			readParamsImpl(dst);
		} catch (Throwable e) {
			LOG.error("read packet fail", e);
		}
		finally{
		}
	}
	
	/**
	 * 读取实现
	 * @param dst 
	 */
	protected abstract void readParamsImpl(byte[] dst) throws Exception;

	/**
	 * 获取包id,可以注解化
	 * @return
	 */
	public abstract int getPacketId();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	

	/**
	 * @return the ctx
	 */
	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	/**
	 * @param ctx the ctx to set
	 */
	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public void run() {
		try {
			runImpl();
		} catch (Throwable e) {
			LOG.error("run packet fail", e);
		}
	}
	/**
	 * 实际运行
	 */
	protected abstract void runImpl();
}
