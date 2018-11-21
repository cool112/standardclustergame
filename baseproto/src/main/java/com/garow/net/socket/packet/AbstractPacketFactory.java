package com.garow.net.socket.packet;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.garow.net.socket.annotation.BzPacket;
import com.garow.net.socket.constants.PacketType;
import com.garow.proto.utils.ClassUtils;
import com.garow.proto.utils.ClassUtils.ClassFilter;

/**
 * 抽象包工厂,利用spring自动注入搜寻的实例
 * @author gjs
 */
public abstract class AbstractPacketFactory <T extends AbstractRecvPacket> implements ApplicationContextAware{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractPacketFactory.class);
	/** 类搜索根路径 */
	private String packageRoot = "com.garow.proto";
	/** 接收包集合 */
	private Map<Integer, T> recvMap = new HashMap<Integer, T>(1000);
	private ApplicationContext app;
	public AbstractPacketFactory() {
//		init();
	}
	/**
	 * 扫描包类的根目录
	 * @param packagePath
	 */
	protected AbstractPacketFactory(String packagePath){
		init(packagePath);
	}
	private void init(String packagePath){
		packageRoot = packagePath;
//		init();
	}
	/**
	 * spring架构下无法使用
	 */
	private void init(){
		List<Class<T>> clazzList = ClassUtils.scanPackage(packageRoot, new ClassFilter() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public boolean accept(Class clazz) {
				if(ClassUtils.isAbstract(clazz))
					return false;
				if(clazz.getSuperclass() != getPacketSupperClass())
					return false;
				Annotation annotation = clazz.getAnnotation(BzPacket.class);
				if(annotation == null)
				{
					LOG.error("warnning! recv packet may forgot setting id! packet:" + clazz.getSimpleName());
					return false;
				}
				if(((BzPacket)annotation).type() != getRcvPacketType())
					return false;
				
				return true;
			}
			
		});
		for(Class<T> clazz : clazzList){
			BzPacket annotation = clazz.getAnnotation(BzPacket.class);
			try {
				addRecvPacket(annotation.id(), clazz.newInstance());
			} catch (Exception e) {
				LOG.error("init recv packet fail! packet:" + clazz.getSimpleName(), e);
			}
		}
	}

	/**
	 * @param id
	 * @param packet
	 */
	protected void addRecvPacket(int id, T packet) {
		recvMap.put(id, packet);
	}

	/**
	 * 获取包实例
	 * @param packetId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T newRecvPacket(int packetId){
		T packet = recvMap.get(packetId);
		if(packet == null)
			return null;
		
		try {
			return (T) packet.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("recv packet instance fail!", e);
			return null;
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.app = applicationContext;
		Map<String, T> beans = app.getBeansOfType(getPacketSupperClass());
		for(T bean : beans.values()) {
			BzPacket anno = bean.getClass().getAnnotation(BzPacket.class);
			if(anno == null) {
				LOG.warn("maybe packet unfinished!:"+bean.getClass().getName());
				continue;
			}
			if(((BzPacket)anno).type() != getRcvPacketType())
				continue;
			
			addRecvPacket(anno.id(), bean);
		}
	}
	
	/**
	 * 包类型
	 * @return
	 */
	protected abstract PacketType getRcvPacketType();
	
	/**
	 * 获取包限定的超类
	 * @return
	 */
	protected abstract Class<T> getPacketSupperClass();
}
