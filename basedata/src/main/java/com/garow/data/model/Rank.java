package com.garow.data.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
/**
 * 排名数据
 * @author seg
 *
 */
@CompoundIndex(def="{'app':1,'score':-1}", unique = true)
public class Rank {
	/**应用名*/
String app;
/**分数*/
int score;
/**人数*/
int count;
public String getApp() {
	return app;
}
public void setApp(String app) {
	this.app = app;
}
public int getScore() {
	return score;
}
public void setScore(int score) {
	this.score = score;
}
public int getCount() {
	return count;
}
public void setCount(int count) {
	this.count = count;
}

}
