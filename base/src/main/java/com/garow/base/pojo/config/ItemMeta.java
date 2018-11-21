package com.garow.base.pojo.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
/**
 * 物品元信息
 * @author seg
 *
 */
public class ItemMeta implements UniqueItem<Integer> {
	@JsonProperty(access = Access.WRITE_ONLY)
	private int id;
	/**具体类型*/
	private String type;
	private String value;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Integer uid() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
