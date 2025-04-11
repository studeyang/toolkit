package io.github.toolkit.commons.model;

import io.github.toolkit.commons.web.AccessInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IcecAccessInfoContext {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(IcecAccessInfoContext.class);

    protected static Class<? extends IcecAccessInfoContext> contextClass = IcecAccessInfoContext.class;
	
	protected static final ThreadLocal<? extends IcecAccessInfoContext> threadLocal = new ThreadLocal<IcecAccessInfoContext>() {
        @Override
        protected IcecAccessInfoContext initialValue() {
            try {
                return contextClass.newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    };
    
    /**
     * 初始化
     * @return
     */
    public static IcecAccessInfoContext initCurrentContext() {
    	IcecAccessInfoContext context = getCurrentContext();
    	context.setAccessInfo(null);
        return context;
    }
    
    public static IcecAccessInfoContext getCurrentContext() {
    	IcecAccessInfoContext context = threadLocal.get();
        return context;
    }
    
    private AccessInfo accessInfo;

    public AccessInfo getAccessInfo() {
        return accessInfo;
    }
    public void setAccessInfo(AccessInfo accessInfo) {
    	this.accessInfo = accessInfo;
    }
}
