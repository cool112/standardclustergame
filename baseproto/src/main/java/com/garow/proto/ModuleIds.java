package com.garow.proto;

/**
 * 模块id常量
 * 
 * @author gjs
 *
 */
public interface ModuleIds {
	/** 校验信息 */
	short AUTH = 1;
	/** 转发/路由 */
	short TRANSFER = 2;
	/** conn  */
	short CONN = 3;
	/** push message  */
	short PUSH = 4;
	/** common message  */
	short COMMON = 5;
}
