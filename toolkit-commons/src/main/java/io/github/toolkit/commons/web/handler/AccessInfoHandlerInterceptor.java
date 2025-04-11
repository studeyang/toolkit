package io.github.toolkit.commons.web.handler;

import io.github.toolkit.commons.model.IcecAccessInfoContext;
import io.github.toolkit.commons.web.AccessInfo;
import io.github.toolkit.commons.web.AccessInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccessInfoHandlerInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccessInfoHandlerInterceptor.class);
	
	private AccessInfoProvider accessInfoProvider;
	
	public void setAccessInfoProvider(AccessInfoProvider accessInfoProvider) {
		this.accessInfoProvider = accessInfoProvider;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		AccessInfo accessInfo = null;
		IcecAccessInfoContext context = IcecAccessInfoContext.initCurrentContext();
		if (null != context.getAccessInfo() && AccessInfoProvider.OnlyFromRequestAccessInfoBuilder.class.equals(accessInfoProvider.getClass())) {
			// 如果已经生成过 AccessInfo
			// 如果 accessInfoProvider 只是 OnlyFromRequestAccessInfoBuilder
			// 就不再重新构建 AccessInfo
			accessInfo = context.getAccessInfo();
		} else if (null != accessInfoProvider) {
			accessInfo = accessInfoProvider.getAccessInfo(request);
		}

		if (null != accessInfo && accessInfo.validate()) {
			context.setAccessInfo(accessInfo);
		} else {
			//TODO 开发过程目前还不能做到每个请求都有AccessInfo
			LOGGER.debug("no valid accessInfo, {} {}!", request.getMethod(), request.getRequestURI());
			//throw new RuntimeException("AccessInfo not found");
		}
		
		return true;
	}
}
