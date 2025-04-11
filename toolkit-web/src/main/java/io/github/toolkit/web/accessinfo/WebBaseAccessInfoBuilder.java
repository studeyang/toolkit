package io.github.toolkit.web.accessinfo;

import io.github.toolkit.commons.web.AccessInfo;
import io.github.toolkit.commons.web.AccessInfoProvider;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * web工程AccessInfo构造器
 * 这个类的存在，是对不经过网关的请求进行补充（主要在开发阶段）
 * AccessId只能在web-agent上生成，这里只需构造用户相关信息
 * 
 * 保留 WebBaseAccessInfoBuilder 是做兼容旧版
 * 
 * @author xujian
 */
public class WebBaseAccessInfoBuilder extends AccessInfoProvider.OnlyFromRequestAccessInfoBuilder implements AccessInfoProvider {

    private static final String STORE_ID_COOKIE_NAME = "storeid";

    @Override
    protected void extendAccessInfo(AccessInfo accessInfo, HttpServletRequest request) {

        setStoreId(accessInfo, request);
    }

    /**
     * 支持基石项目多店铺改造，多店铺情况下，前端会选择一个店铺并存储在浏览器 Cookie 中
     * @param accessInfo
     * @param request
     */
    private void setStoreId(AccessInfo accessInfo, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (STORE_ID_COOKIE_NAME.equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {

                String[] values = cookie.getValue().split("\\|");

                if (values.length < 3 || !StringUtils.hasText(accessInfo.getUserLoginId())) {
                    return;
                }

                if (accessInfo.getUserLoginId().equals(values[2])) {
                    accessInfo.setProductStoreId(values[0]);
                    accessInfo.setStoreInternalId(values[1]);
                }

                return;
            }
        }
    }
}
