package com.garow.tcpgateway;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.garow.net.socket.server.SimpleNioServer;
import com.garow.net.socket.server.config.ProtoConfigContext;
import com.garow.net.socket.server.handler.PacketExecuteHandler;
import com.garow.tcpgateway.server.handler.PacketParseHandler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LtvCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Configuration
@ComponentScan({ "com.garow" })
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class Application implements CommandLineRunner {
	@Resource
	private SimpleNioServer simpleNioServer;

	@Bean
	public ChannelHandler nioServerHandler() {
		return new LoggingHandler("tcpgateway", LogLevel.INFO);
	}

	@Bean
	public ChannelHandler bzHandler() {
		return new PacketExecuteHandler();
	}

	@Bean
	public ChannelHandler packetParser() {
		return new PacketParseHandler();
	}

	@Bean
	public ChannelHandler codec() {
		return new LtvCodec();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		simpleNioServer.start(ProtoConfigContext.getServerConfig());
	}
}