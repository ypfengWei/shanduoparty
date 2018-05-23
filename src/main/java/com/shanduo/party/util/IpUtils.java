package com.shanduo.party.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 类说明
 * @Description
 * @author 作者 : Fan van
 * @version 创建时间 : 2017年12月13日 下午8:22:57
 * 
 */
public class IpUtils {

	// 获取客户端ip
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		// 多次反向代理后会有多个ip值，第一个ip才是真实ip
		int index = ip.indexOf(",");
		if (index != -1) {
			ip = ip.substring(0, index);
		}
		return ip;
	}

}
