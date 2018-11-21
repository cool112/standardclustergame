package com.garow.gameserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan({"com.garow"})
@EnableEurekaClient
@SpringBootApplication
@EnableZuulProxy
public class Application {
	/**
	 * 使用ribbon的rest客户端
	 * @return
	 */
	@Bean
    @LoadBalanced
    RestTemplate restTemplate() {
	return new RestTemplate();
   }
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
