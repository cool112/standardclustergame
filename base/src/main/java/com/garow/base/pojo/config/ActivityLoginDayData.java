package com.garow.base.pojo.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 登录活动配置数据，结构为：
 * {
 * app1:[],
 * app2:[]
 * }
 * @author seg
 *
 */
public class ActivityLoginDayData implements TransitionData{
	/**
	 * 名为test的应用的登录奖励列表
	 */
	private List<RewardData> test;
	/**
	 * appMap.appName = rewardByDay
	 */
	private Map<String, List<RewardData>> appMap = new HashMap<String, List<RewardData>>();

	public List<RewardData> getTest() {
		return test;
	}

	public void setTest(List<RewardData> test) {
		this.test = test;
	}

	@Override
	public void postInit() {
		appMap.put("test", test);
		test = null;
	}

	public Map<String, List<RewardData>> getAppMap() {
		return appMap;
	}
	
}
