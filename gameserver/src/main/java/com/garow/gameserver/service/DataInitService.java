package com.garow.gameserver.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.garow.base.pojo.config.ActivityLoginDayData;
import com.garow.base.pojo.config.ItemData;
import com.garow.base.pojo.config.ItemMeta;
import com.garow.base.service.ConfigClientService;
import com.garow.base.service.DataRefreshService;
import com.garow.gameserver.config.ActivityConfig;
/**
 * 数据初始化服务，同时也实现了刷新事件的接口
 * @author seg
 *
 */
@Service
public class DataInitService implements DataRefreshService {
	@Resource
	private ActivityConfig			activityConfig;
	@Resource
	private ConfigClientService		configClientService;

	private ItemData				itemData;

	private ActivityLoginDayData	loginCountData;

	@PostConstruct
	void init() {
		itemData = configClientService.getFile(activityConfig.getItemConf(), ItemData.class);
		loginCountData = configClientService.getFile(activityConfig.getLogindayConf(), ActivityLoginDayData.class);
	}

	public ItemData getItemData() {
		return itemData;
	}

	@Override
	public void refresh() {
		init();
	}

	public ActivityLoginDayData getLoginCountData() {
		return loginCountData;
	}
	/**
	 * 转化奖励内容，针对复合奖励
	 * @param itemMeta
	 * @return
	 */
	public List<ItemMeta> transfer(ItemMeta itemMeta){
		List<ItemMeta> rewardList = new ArrayList<ItemMeta>();
		switch (itemMeta.getType()) {
		case "diamond":
			rewardList.add(itemMeta);
			break;
		case "composite":
			//TODO value可能是集合
			break;

		default:
			return Collections.EMPTY_LIST;
		}
		return rewardList;
	}
}
