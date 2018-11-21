package com.garow.notifyserver.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.garow.notifyserver.event.AbstractPushEvent;

import io.netty.util.internal.ConcurrentSet;
/**
 * 用户状态缓存
 * @author seg
 *
 */
public class UserStateCache {
	/**
	 * userStateMap.app.userId=state
	 */
	private static Map<String, Map<String, UserStateEntity>> userStateMap = new ConcurrentHashMap<String, Map<String, UserStateEntity>>();
	/**
	 * 延迟事件
	 * delayEvents.app.userId=events
	 */
	private static Map<String, Map<String, Set<AbstractPushEvent>>> delayEvents = new ConcurrentHashMap<String, Map<String, Set<AbstractPushEvent>>>();
	/**
	 * 修改用户状态
	 * @param sessionId
	 * @param app
	 * @param userId
	 * @param state
	 */
	public static void updateUser(String sessionId, String app, String userId, int state) {
		UserStateEntity user = getUserState(app, userId);
		user.setState(state);
		user.setSessionId(sessionId);
		user.setLastModTime(System.currentTimeMillis());
	}
	/**
	 * 获取用户状态
	 * @param app
	 * @param userId
	 * @return
	 */
	public static UserStateEntity getUserState(String app, String userId) {
		Map<String, UserStateEntity> userMap = userStateMap.get(app);
		if (userMap == null) {
			userMap = new ConcurrentHashMap<String, UserStateEntity>();
			Map<String, UserStateEntity> oldMap = userStateMap.putIfAbsent(app, userMap);
			if(oldMap != null)
				userMap = oldMap;
		}
		UserStateEntity user = userMap.get(userId);
		if(user == null) {
			user = new UserStateEntity();
			UserStateEntity oldUser = userMap.putIfAbsent(userId, user);
			if(oldUser != null)
				user = oldUser;
		}
		return user;
	}
	/**
	 * 增加延迟推送事件
	 * @param app
	 * @param userId
	 * @param event
	 */
	public static void addDelayEvent(String app, String userId, AbstractPushEvent event) {
		Map<String, Set<AbstractPushEvent>> userMap = delayEvents.get(app);
		if (userMap == null) {
			userMap = new ConcurrentHashMap<String, Set<AbstractPushEvent>>();
			Map<String, Set<AbstractPushEvent>> oldMap = delayEvents.putIfAbsent(app, userMap);
			if(oldMap != null)
				userMap = oldMap;
		}
		Set<AbstractPushEvent> events = userMap.get(userId);
		if(events == null) {
			events = new ConcurrentSet<AbstractPushEvent>();
			Set<AbstractPushEvent> oldEvents = userMap.putIfAbsent(userId, events);
			if(oldEvents != null)
				events = oldEvents;
		}
		events.add(event);
	}
	/**
	 * 获取所有延迟推送事件
	 * @param app
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection<AbstractPushEvent> getDelayEvents(String app, String userId){
		Map<String, Set<AbstractPushEvent>> userMap = delayEvents.get(app);
		if (userMap == null) {
			return Collections.EMPTY_SET;
		}
		Set<AbstractPushEvent> events = userMap.get(userId);
		if(events == null) {
			return Collections.EMPTY_SET;
		}
		return events;
	}
	/**
	 * 移除并返回所有延迟推送事件
	 * @param app
	 * @param userId
	 * @return
	 */
	public static Collection<AbstractPushEvent> removeDelayEvents(String app, String userId){
		Map<String, Set<AbstractPushEvent>> userMap = delayEvents.get(app);
		if (userMap == null) {
			return Collections.EMPTY_SET;
		}
		Set<AbstractPushEvent> events = userMap.remove(userId);
		if(events == null) {
			return Collections.EMPTY_SET;
		}
		return events;
	}
}
