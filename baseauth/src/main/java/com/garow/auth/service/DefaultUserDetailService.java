	package com.garow.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.garow.data.model.User;
import com.garow.data.service.PlayerSvc;
import com.garow.data.service.UserSvc;
/**
 * 用户信息服务,需要auth先注册,然后通过login验证,注意session是通过header:sessionid=xxx传递
 * @author seg
 *
 */
@Service
public class DefaultUserDetailService implements UserDetailsService {
	/**old compatible*/
	@Autowired(required = true)
	private PlayerSvc playerSvc;
	@Autowired
	private UserSvc userSvc;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		List<Player> roles = playerSvc.findByDevId(username);
//		return new GameUserDetail(roles);
		User user = userSvc.findByDeviceId(username);
		return new GameUserDetail(user);
	}
	
}
