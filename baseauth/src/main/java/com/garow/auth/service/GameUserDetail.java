package com.garow.auth.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.garow.data.model.AppInfo;
import com.garow.data.model.Player;
import com.garow.data.model.User;
/**
 * 用户权限相关数据,目前把游戏类型作为role
 * @author seg
 *
 */
public class GameUserDetail implements UserDetails {
	private static final long serialVersionUID = 756533047923047235L;
	 private List<SimpleGrantedAuthority> auhorities = new LinkedList<>();
	 private List<Player> players;
	 private String username;
	 private User user;
	 
	public GameUserDetail(List<Player> players) {
		super();
		this.players = players;
		if(this.players != null && !this.players.isEmpty()) {
			for(Player player : this.players) {
				auhorities.add(new SimpleGrantedAuthority(player.getApp()));
			}
			username = this.players.get(0).getDeviceId();
		}
	}
	
	

	public GameUserDetail(User user) {
		super();
		if(user == null)
			return;
		this.user = user;
		username = this.user.getDeviceId();
		Map<String, AppInfo> apps = user.getPlayers();
		if(apps == null || apps.isEmpty())
			return;
		
		for(String appName:apps.keySet()) {
			auhorities.add(new SimpleGrantedAuthority(appName));
		}
	}



	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return auhorities;
	}

	@Override
	public String getPassword() {
		return "";//没有密码
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
