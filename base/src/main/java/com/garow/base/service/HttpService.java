package com.garow.base.service;

import javax.annotation.Resource;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.garow.base.constants.SysConstants;
/**
 * http的客户端服务，封装了sessionid的设置
 * @author seg
 *
 */
@Service
public class HttpService {
	@Resource
	private RestTemplate normalRestTemplate;
	
	public <T> ResponseEntity<T> post(String url, String body, String sessionId, Class<T> respones){
		HttpHeaders headers = new HttpHeaders();
		headers.add(SysConstants.SESSION_KEY, sessionId);
		HttpEntity<String> entity = new HttpEntity<String>(body, headers);
		ResponseEntity<T> result = normalRestTemplate.exchange(url,  HttpMethod.POST, entity, respones);
		return result;
	}
	
	public <T> ResponseEntity<T> get(String url, String body, String sessionId, Class<T> respones){
		HttpHeaders headers = new HttpHeaders();
		headers.add(SysConstants.SESSION_KEY, sessionId);
		HttpEntity<String> entity = new HttpEntity<String>(body, headers);
		ResponseEntity<T> result = normalRestTemplate.exchange(url,  HttpMethod.GET, entity, respones);
		return result;
	}
}
