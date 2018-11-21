package com.garow.gameserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
/**
 * 活动配置
 * @author seg
 *
 */
@Configuration
@ConfigurationProperties("game.activity")
public class ActivityConfig {
	/**开关*/
	private boolean	enable;
	/**物品配置路径*/
	private String	itemConf;
	/**登录奖励配置路径*/
	private String	logindayConf;
	/**登录奖励重置周期，0代表非周期*/
	private int		logindayReset;

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getItemConf() {
		return itemConf;
	}

	public void setItemConf(String itemConf) {
		this.itemConf = itemConf;
	}

	public String getLogindayConf() {
		return logindayConf;
	}

	public void setLogindayConf(String logincountConf) {
		this.logindayConf = logincountConf;
	}

	public int getLogindayReset() {
		return logindayReset;
	}

	public void setLogindayReset(int logindayReset) {
		this.logindayReset = logindayReset;
	}

}
