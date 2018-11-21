package com.garow.base.pojo.config;
/**
 * 具有唯一id的配置元素
 * @author seg
 *
 * @param <T>
 */
public interface UniqueItem<T> {
	/**
	 * 唯一id
	 * @return
	 */
	public T uid();
}
