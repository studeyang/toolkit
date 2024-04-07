package io.github.open.toolkit.commons.web;

import io.github.open.toolkit.commons.model.IcecAccessInfoContext;
import io.github.open.toolkit.commons.utils.RequestUtil;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public interface AccessInfoProvider {
	
	public AccessInfo getAccessInfo(HttpServletRequest request);
	
	
	public static abstract class AccessInfoBuilder implements AccessInfoProvider {
		
		private String defaultAccessId = "";
		
		/* 暂不开放设置AccessId
		public void setDefaultAccessId(String defaultAccessId) {
			this.defaultAccessId = defaultAccessId;
		}*/

		@Override
		public AccessInfo getAccessInfo(HttpServletRequest request) {
			
			AccessInfo accessInfo = AccessInfo.fromHeader(request.getHeader(AccessInfo.HEADER_KEY));
			
			if (null == accessInfo) 
				accessInfo = obtainAccessInfo();
			
			if (null == accessInfo) {
				return null;
			} else {
				IcecAccessInfoContext.getCurrentContext().setAccessInfo(accessInfo);
			}
			
			if (!StringUtils.hasText(accessInfo.getAccessId())) {
				accessInfo.setAccessId(this.defaultAccessId);
			}
			
			if (!StringUtils.hasText(accessInfo.getIpAddress())) {
				accessInfo.setIpAddress(RequestUtil.getIpAddress(request));
			}
			
			if (!StringUtils.hasText(accessInfo.getReferer())) {
				String referer = request.getHeader(AccessConstant.REQUEST_REFERER);
				if (null == referer) referer = "";
				
				// 如果有参数的，把参数截掉，因为可能存在中文，header写入中文会报错
				if (referer.indexOf("?") > 0) {
					referer = referer.substring(0, referer.indexOf("?"));
				}
				if (referer.indexOf("#") > 0) {
					referer = referer.substring(0, referer.indexOf("#"));
				}
				
				accessInfo.setReferer(referer);
			}
			
			extendAccessInfo(accessInfo, request);
			
			return accessInfo;
		}
		
		protected abstract AccessInfo obtainAccessInfo();
		
		/**
		 * 扩展
		 * @param accessInfo
		 * @param request
		 */
		protected void extendAccessInfo(AccessInfo accessInfo, HttpServletRequest request) {
			//默认什么都不加
		}
		
	}
	
	public static class OnlyFromRequestAccessInfoBuilder extends AccessInfoBuilder implements AccessInfoProvider {

		@Override
		protected AccessInfo obtainAccessInfo() {
			return null;
		}
		
	}
	
}
