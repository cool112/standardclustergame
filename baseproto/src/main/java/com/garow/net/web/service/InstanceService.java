package com.garow.net.web.service;

import java.util.List;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.garow.net.socket.server.config.ServerConfig;
import com.garow.net.web.utils.IpUtils;

/**
 * 实例服务
 * 
 * @author seg
 *
 */
@Service
public class InstanceService {
	@Value("${server.port}")
	private int				httpPort;
	@Resource // 考虑非tcp服务的引用
	private ServerConfig	serverConfig;

	private String			tcpHost;

	private String			httpHost;

	private String			host;

	private String			tcpWanHost;

	@PreDestroy
	void destory() {
		host = null;
		tcpHost = null;
		httpHost = null;
		tcpWanHost = null;
	}

	/**
	 * 获取tcp监听信息
	 * 
	 * @return
	 */
	public String getTcpHost() {
		if (tcpHost == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(getHost()).append(":").append(serverConfig.getListen());
			tcpHost = sb.toString();
		}
		return tcpHost;
	}

	/**
	 * 获取http监听信息
	 * 
	 * @return
	 */
	public String getHttpHost() {
		if (httpHost == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(getHost()).append(":").append(httpPort);
			httpHost = sb.toString();
		}
		return httpHost;
	}

	private String getLocalIp() {
		List<String> ips = IpUtils.getLocalHost(serverConfig.getLoNetPrefix());
		String ip = "";
		if (!ips.isEmpty())
			ip = ips.get(0);
		return ip;
	}

	public String getHost() {
		if (host == null) {
			String ip = getLocalIp();
			if (StringUtils.isEmpty(ip))
				return "";
			host = ip;
		}
		return host;
	}

	public String getTcpWanHost() {
		if (tcpWanHost == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(serverConfig.getHostname()).append(":").append(serverConfig.getListen());
			tcpWanHost = sb.toString();
		}
		return tcpWanHost;
	}
}
