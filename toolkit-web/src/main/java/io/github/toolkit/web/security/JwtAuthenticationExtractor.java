package io.github.toolkit.web.security;

import io.github.toolkit.commons.utils.JsonUtil;
import io.github.toolkit.commons.web.security.IcecAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 通过解析 JWT 得到用户身份，无需外部调用，理论上性能更高
 *
 * @author jian.xu
 * @version v1.2_20200520
 */
public class JwtAuthenticationExtractor implements HttpAuthenticationExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationExtractor.class);
    private static final String SC_COOKIE_NAME = "security_context";
    private static final String JWT_USERNAME_KEY = "sub";

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public boolean supportRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies || cookies.length == 0) {
            return false;
        }
        for (Cookie cookie : cookies) {
            if (SC_COOKIE_NAME.equals(cookie.getName())) {
                return StringUtils.hasText(cookie.getValue());
            }
        }
        return false;
    }

    @Override
    public Authentication extractAuthentication(HttpServletRequest request) {
        Map<String, Object> payload = null;
        for (Cookie cookie : request.getCookies()) {
            if (SC_COOKIE_NAME.equals(cookie.getName())) {
                payload = JwtVerifier.decryptToken(cookie.getValue());
            }
        }

        if (null == payload || !payload.containsKey(JWT_USERNAME_KEY)) {
            LOG.error("不是一个有效的新版本 SC_JWT");
            return null;
        }

        /**
         * 把sub的内容转移到 IcecAuthentication.USERNAME_KEY
         */
        payload.put(IcecAuthentication.USERNAME_KEY, payload.remove(JWT_USERNAME_KEY));

        return new IcecAuthentication(payload);
    }



    static class JwtVerifier {
        private static final String RSA_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCutIGMOO+WgQjYQmZGwIlgIdF4\n" +
                "KyQoUisiibZnois+ko/bKeWw+S737Hjog+6qfaAu/TCb3SbBJGMYcbUAsit+65EN\n" +
                "8X7x0Z2JoXSejSyEyWEn6UpMGyLUSi2z2OHF4hzsDGxkGagHbgkpF9I+LzLyMFKT\n" +
                "dX0SdZkx8N3Z0Z/brQIDAQAB" +
                "\n-----END PUBLIC KEY-----";

        private static final SignatureVerifier VARIFIER = new RsaVerifier(RSA_PUBLIC_KEY);

        static Map<String, Object> decryptToken(String token) {
            try {
                Jwt jwt = JwtHelper.decodeAndVerify(token, VARIFIER);
                if (null != jwt) {
                    return JsonUtil.deserialize(jwt.getClaims());
                }
            } catch (Exception e) {
                //nothing to do
            }

            return null;
        }
    }
}
