package com.garow.base.pojo.config;
/**
 * 需要转换的数据，由于从excel转来的json数据有格式限制，数组到map的转化要通过代码完成。
 * ConfigClientService。getFile 会自动调用
 * @author seg
 *
 */
public interface TransitionData {
	/**
	 * 实例化之后调用
	 */
	public void postInit();
}
