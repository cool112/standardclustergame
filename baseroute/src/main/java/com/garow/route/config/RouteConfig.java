package com.garow.route.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.garow.net.socket.server.handler.PacketExecuteHandler;
import com.garow.route.handler.InternalPacketParser;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LtvCodec;

/**
 * 路由配置
 * @author seg
 *
 */
@Configuration
@ConfigurationProperties("route")
public class RouteConfig {
	/**tcp应用列表，表示需要参与到路由功能中的应用*/
	private List<String> apps;
	/**
	 * 内部编解码器
	 * @return
	 */
	@Bean
	public ChannelHandler innerCodec() {
		return new LtvCodec();
	}
	/**
	 * 内部业务包解析器
	 * @return
	 */
	@Bean
	public ChannelHandler innerPacketParser() {
		return new InternalPacketParser();
	}
	/**
	 * 内部业务处理
	 * @return
	 */
	@Bean
	public ChannelHandler innerBzHandler() {
		return new PacketExecuteHandler();
	}

	public List<String> getApps() {
		return apps;
	}

	public void setApps(List<String> apps) {
		this.apps = apps;
	}
	

}
