package com.garow.base.view.activity;

import java.util.List;

import com.garow.base.pojo.config.ItemMeta;

/**
 * 奖励槽视图，合并了领取信息和奖励内容
 * 
 * @author Administrator
 *
 */
public class RewardSlotView {
	String			key	= "";
	boolean			received;
	/**奖励集合*/
	List<ItemMeta>	rewards;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	public List<ItemMeta> getRewards() {
		return rewards;
	}

	public void setRewards(List<ItemMeta> rewards) {
		this.rewards = rewards;
	}

}
