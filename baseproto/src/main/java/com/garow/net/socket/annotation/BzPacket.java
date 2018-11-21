/**
 * 
 */
package com.garow.net.socket.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import com.garow.net.socket.constants.PacketType;

/**
 * 业务包注解
 * @author gjs
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
public @interface BzPacket {
	/** id */
	int id();
	/** 类型 如客户端到网关 大厅到网关 */
	PacketType type();
}
