package com.garow.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;

/**
 * http session 配置
 * @author seg
 *
 */
@Configuration
@EnableMongoHttpSession(collectionName="sessions", maxInactiveIntervalInSeconds=1800)
public class HttpSessionConfig {
	@Bean
	public HeaderHttpSessionIdResolver cookieHttpSessionIdResolver() {
		return new HeaderHttpSessionIdResolver("sessionid");
	}
}
