package com.garow.logserver.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * mybatis生成表配置
 * 
 * @author gjs
 *
 */
@Configuration
@ComponentScan(basePackages = { "com.gitee.sunchenbin.mybatis.actable.manager.*" })
@EnableScheduling
public class ActableConfig {
	private static final Logger	LOG	= LoggerFactory.getLogger(ActableConfig.class);
	@Value("${spring.datasource.driver-class-name}")
	private String				driver;

	@Value("${spring.datasource.url}")
	private String				url;

	@Value("${spring.datasource.username}")
	private String				username;

	@Value("${spring.datasource.password}")
	private String				password;	
	@Value("${spring.datasource.maxActive:30}")
	private int					maxActive;
	@Value("${spring.datasource.minIdle:1}")
	private int					minIdle;
	@Value("${spring.datasource.initialSize:1}")
	private int					initialSize;
	@Autowired
	private MybatisConfig		mybatisConfig;

	@Bean
	public MybatisConfig configProperties() throws Exception {
		return mybatisConfig;
	}

	@Bean
	public DruidDataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setMaxActive(maxActive);
		dataSource.setMinIdle(minIdle);
		dataSource.setInitialSize(initialSize);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setTestOnBorrow(true);
		return dataSource;
	}

	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager() {
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
		dataSourceTransactionManager.setDataSource(dataSource());
		return dataSourceTransactionManager;
	}

	@Bean
	public SqlSessionFactoryBean sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(
				resolver.getResources("classpath*:com/gitee/sunchenbin/mybatis/actable/mapping/*/*.xml"));
		sqlSessionFactoryBean.setTypeAliasesPackage("com.garow.logserver.dao.model.*");
		return sqlSessionFactoryBean;
	}
	/**
	 * 线程池，基本一个连接提供一个线程
	 * @return
	 */
	@Bean
	public ThreadPoolTaskExecutor threadPool() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
		threadPool.setMaxPoolSize(maxActive);
		threadPool.setQueueCapacity(100000);
		threadPool.setWaitForTasksToCompleteOnShutdown(true);
		return threadPool;
	}
	/**
	 * 定时器，默认只给一个线程
	 * @return
	 */
	@Bean
	public ScheduledExecutorService scheduler() {
		return Executors.newScheduledThreadPool(1);
	}

}
