package com.garow.logserver.config;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(ActableConfig.class)
public class ActableMapperConfig {
	@Bean
    public MapperScannerConfigurer mapperScannerConfigurer() throws Exception{
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.garow.logserver.dao.*;com.gitee.sunchenbin.mybatis.actable.dao.*;com.garow.logserver.generator.*");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return mapperScannerConfigurer;
    }

}
