package com.garow.base.utils;

import java.util.Map;
/**
 * rest工具
 * @author seg
 *
 */
public class WebUtils {
	/**
	 * 参数不为空检查
	 * @param params 当json参数在body的通用转换
	 * @param query
	 * @return
	 */
	public static boolean checkParamters(Map<String, Object> params, Object...query) {
		if(params == null && !checkNull(query))
			return false;
		return true;
	}
	
	public static boolean checkNull(Object...query) {
		if(query == null)
			return true;
		for( Object arg : query) {
			if(arg == null)
				return false;
		}
		return true;
	}
}
