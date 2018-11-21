package com.garow.base.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.garow.base.service.BaseServiceContext;
import com.garow.base.service.ConfigClientService;
/**
 * 基础配置
 * @author seg
 *
 */
@Configuration
public class BaseConfig {
	/**
	 * 不带负载均衡的restTemplate
	 * @return
	 */
	@Bean
	public RestTemplate normalRestTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	@ConditionalOnProperty(value = "spring.cloud.config.enabled", matchIfMissing = true)
	ConfigClientService configClientService(ConfigClientProperties configClientProperties) {
		return new ConfigClientService(configClientProperties);
	}
}
