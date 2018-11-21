package com.garow.notifyserver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.garow.net.socket.server.SimpleNioServer;
import com.garow.net.socket.server.config.ProtoConfigContext;
import com.garow.net.socket.server.handler.PacketExecuteHandler;
import com.garow.route.handler.InternalPacketParser;

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

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public ChannelHandler nioServerHandler() {
		return new LoggingHandler("notifyserver", LogLevel.INFO);
	}

	@Bean
	public ChannelHandler bzHandler() {
		return new PacketExecuteHandler();
	}

	@Bean
	public ChannelHandler packetParser() {
		return new InternalPacketParser();
	}

	@Bean
	public ChannelHandler codec() {
		return new LtvCodec();
	}

	@Bean
	public ThreadPoolTaskExecutor threadPool() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
		threadPool.setMaxPoolSize(3);
		threadPool.setQueueCapacity(100000);
		threadPool.setWaitForTasksToCompleteOnShutdown(true);
		return threadPool;
	}

	@Bean
	public ScheduledExecutorService scheduler() {
		return Executors.newScheduledThreadPool(2);
	}

	@Override
	public void run(String... args) throws Exception {
		simpleNioServer.start(ProtoConfigContext.getServerConfig());
	}
}
