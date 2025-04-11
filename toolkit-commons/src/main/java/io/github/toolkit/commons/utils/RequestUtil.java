package io.github.toolkit.commons.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

public class RequestUtil {
	
	private final static AntPathMatcher PATH_MATCHER;
	static {
		PATH_MATCHER = new AntPathMatcher();
		PATH_MATCHER.setTrimTokens(false);
		PATH_MATCHER.setCaseSensitive(true);
	}

	public static String getRequestPath(HttpServletRequest request) {
		String url = request.getServletPath();

		if (request.getPathInfo() != null) {
			url += request.getPathInfo();
		}

		return url;
	}
	
	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		
		return null;
	}
	
	public static boolean matchs(HttpServletRequest request, String... patterns) {	
		return matchs(getRequestPath(request), patterns);
	}
	
	public static boolean matchs(String path, String... patterns) {
		for (String pattern : patterns) {
			if (PATH_MATCHER.match(pattern, path)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String getIpAddress(HttpServletRequest request) {
	    return getIpAddress(request, false);
	}
	/**
	 * 获取IP地址，优先从 x-forwarded-for 中提取
	 * @param request
	 * @param last： true:取最后一个, false:取第一个
	 * @return
	 */
	public static String getIpAddress(HttpServletRequest request, boolean last) {
		String ip = request.getHeader("x-forwarded-for");
		
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getRemoteAddr();
	    }
	    
	    if (ip.indexOf(",") > 0) {
	    	String[] ips = ip.split(",");
	    	if (!last) {
	    		ip = ips[0].trim();
	    	} else {
	    		ip = ips[ips.length - 1].trim();
	    	}
	    }
	    return (!StringUtils.hasText(ip) || ip.equals("0:0:0:0:0:0:0:1")) ? "127.0.0.1" : ip;
	}
}
