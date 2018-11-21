package com.garow.net.web.utils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ip工具
 * @author gjs
 *
 */
public class IpUtils {
	private static final Logger LOG = LoggerFactory.getLogger(IpUtils.class);
	/**
	 * 获取本地可用ipv4地址
	 * @param prefix 限制前缀,针对多个网卡的情况,可选
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getLocalHost(String prefix){
		List<String> ips = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> allif = NetworkInterface.getNetworkInterfaces();
			while (allif.hasMoreElements()) {
				NetworkInterface ethif = (NetworkInterface) allif
						.nextElement();
				Enumeration<InetAddress> addrs = ethif.getInetAddresses();
				while(addrs.hasMoreElements()){
					InetAddress addr = addrs.nextElement();
					if(addr.isLoopbackAddress())
						continue;
					if(addr instanceof Inet6Address)
						continue;
					if(!addr.isSiteLocalAddress())
						LOG.info("customize ip type:" + addr.getHostAddress());
					String ip = addr.getHostAddress();
					if(prefix != null && !prefix.equals("") && ip.indexOf(prefix) == -1)
						continue;
					ips.add(addr.getHostAddress());
				}
			}
		} catch (SocketException e) {
			return Collections.EMPTY_LIST;
		}
		return ips;
	}
	/**
	 * string to SocketAddress
	 * @param host
	 * @return
	 */
	public static SocketAddress parseAddr(String host) {
		String[] ipAndPort = host.split(":");
		if(ipAndPort.length < 2)
			return null;
		try {
			String ip = ipAndPort[0];
			int port = Integer.parseInt(ipAndPort[1]);
			return InetSocketAddress.createUnresolved(ip, port);
		} catch (Exception e) {
			LOG.error("invalid host:" + host, e);
			return null;
		}
	}
}
