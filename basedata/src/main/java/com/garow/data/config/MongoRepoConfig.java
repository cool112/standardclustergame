package com.garow.data.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
/**
 * mongodb 仓库配置
 * @author seg
 *
 */
@Configuration
@EnableMongoRepositories({"com.garow"})
public class MongoRepoConfig {

}
