package com.garow.logserver.config;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * 使用了config server不方便获取所有配置参数，这个类是做兼容用的，actab包使用
 * @author seg
 *
 */
@Component
public class MybatisConfig extends HashMap<String, String>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8980716335867459744L;
	@Value("${mybatis.database.type}")
	public String databaseType;
	@Value("${mybatis.model.pack}")
	public String pack;
	@Value("${mybatis.table.auto:none}")
	private String tableAuto;
	@PostConstruct
	void init() {
		put("mybatis.database.type", databaseType);
		put("mybatis.model.pack", pack);
		put("mybatis.table.auto", tableAuto);
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	
}
