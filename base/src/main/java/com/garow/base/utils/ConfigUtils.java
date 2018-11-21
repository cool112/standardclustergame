package com.garow.base.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.garow.base.pojo.config.UniqueItem;
/**
 * 配置工具类
 * @author seg
 *
 */
public class ConfigUtils {
	/**
	 * list to map
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T, P extends UniqueItem<T>> Map<T, P> transit(List<P> list) {
		if (list == null || list.isEmpty())
			return Collections.EMPTY_MAP;

		Map<T, P> map = new HashMap<T, P>();
		for (UniqueItem<T> item : list) {
			T id = item.uid();
			map.put(id, (P) item);
		}
		return map;
	}
}
