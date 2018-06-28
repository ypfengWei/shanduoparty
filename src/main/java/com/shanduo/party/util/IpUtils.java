package com.shanduo.party.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取请求的IP
 * @ClassName: IpUtils
 * @Description: TODO
 * @author fanshixin
 * @date 2018年6月28日 上午10:19:17
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
