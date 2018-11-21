package com.garow.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.NullRequestCache;

import com.garow.auth.service.DefaultUserDetailService;
/**
 * 安全配置
 * @author seg
 *
 */
@Configuration
@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	/**
	 * 目前版本不使用密码
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@Bean
	public NoOpPasswordEncoder passwordEncoder() {
	  return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}
	@Autowired
    PasswordEncoder passwordEncoder;
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
             . antMatchers(HttpMethod.GET, "/preauth").permitAll()//测试时增加的path需要在生产环境去掉
               .antMatchers(HttpMethod.POST, "/preauth").permitAll()
               .anyRequest().authenticated()
//               .and()
//               .formLogin().loginPage("/login")
//               .usernameParameter("user")
//               .passwordParameter("pwd")
//               .defaultSuccessUrl("/postauth")
//               .failureUrl("/login?error=true")
//               .permitAll()
//               .and().logout()
//               .logoutSuccessUrl("/login?logout=true")
//               .invalidateHttpSession(true)
//               .permitAll()
               .and().requestCache().requestCache(new NullRequestCache())
               .and().httpBasic()
               .and().csrf().disable();

    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
        authManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder);
    }
    
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new DefaultUserDetailService();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
