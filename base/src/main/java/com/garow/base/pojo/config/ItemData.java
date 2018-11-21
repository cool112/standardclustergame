package com.garow.base.pojo.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.garow.base.utils.ConfigUtils;
/**
 * 物品配置数据
 * {
 * 	categoty1:[],
 * categoty2:[]
 * }
 * @author seg
 *
 */
public class ItemData implements TransitionData{
	private List<ItemMeta> currency;
	private Map<Integer, ItemMeta> currencyMap;
	/**
	 * itemMap.categoty.id=itemMeta
	 */
	private Map<String, Map<Integer, ItemMeta>> itemMap = new HashMap<>();

	public List<ItemMeta> getCurrency() {
		return currency;
	}

	public void setCurrency(List<ItemMeta> currency) {
		this.currency = currency;
	}

	@Override
	public void postInit() {
		currencyMap = ConfigUtils.transit(currency);
		currency = null;
		itemMap.put("currency", currencyMap);
	}

	public Map<Integer, ItemMeta> getCurrencyMap() {
		return currencyMap;
	}

	public Map<String, Map<Integer, ItemMeta>> getItemMap() {
		return itemMap;
	}
	
	
}
