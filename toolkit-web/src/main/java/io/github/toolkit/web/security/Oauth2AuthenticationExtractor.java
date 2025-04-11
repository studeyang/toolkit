//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.github.toolkit.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.toolkit.commons.web.security.IcecAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 通过使用 access_token 调用远程服务获取身份信息
 * 增加了一层 redis cache，以提升并发处理能力
 *
 * @author jian.xu
 * @version v1.2_20200520
 */
public class Oauth2AuthenticationExtractor implements HttpAuthenticationExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(Oauth2AuthenticationExtractor.class);

	private final String userInfoEndpointUrl;
	private final AuthenticationCache<IcecAuthentication> authCache;
    private final ObjectMapper objectMapper = new ObjectMapper();

	public Oauth2AuthenticationExtractor(RedisConnectionFactory redisFactory, String userInfoEndpointUrl) {
		this.userInfoEndpointUrl = userInfoEndpointUrl;
		this.authCache = new IcecAuthenticationRedisCache(redisFactory, objectMapper);
	}

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public boolean supportRequest(HttpServletRequest request) {
        return null != request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    @Override
    public Authentication extractAuthentication(HttpServletRequest request) {
        String authStr = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authStr)) {

            LOG.warn("认证头信息为空。");
            return null;

        } else {

            Authentication authentication = loadAuthenticationFromCache(authStr);
            if (null != authentication) {
                LOG.debug("cached Authentication: " + authentication + "(" + authentication.getPrincipal() + ")");
                return authentication;
            }

            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            try {
                ClientHttpRequest userInfoRequest = requestFactory.createRequest(new URI(this.userInfoEndpointUrl), HttpMethod.GET);
                userInfoRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, authStr);
                ClientHttpResponse response = userInfoRequest.execute();
                if (response.getStatusCode().series().equals(Series.SUCCESSFUL)) {
                    Map<String, Object> map = (Map) this.objectMapper.readValue(response.getBody(), Map.class);
                    if (null != map) {
                        authentication = new IcecAuthentication(map);
                    }
                }
            } catch (Exception e) {
                LOG.error("call userinfo service error!", e);
            }

            if (null != authentication) {
                LOG.debug("load new Authentication: " + authentication + "(" + authentication.getPrincipal() + ")");
                this.authCache.putAuthentication(authStr, authentication);
                return authentication;
            }

            return null;
        }
    }

    private Authentication loadAuthenticationFromCache(String authStr) {

        Authentication auth = this.authCache.getAuthentication(authStr);
        if (null != auth && LOG.isDebugEnabled()) {
            return auth;
        }
        return null;
    }





    static class IcecAuthenticationRedisCache implements AuthenticationCache<IcecAuthentication> {
        private static final String AUTHENTICATION_CACHE_MAP_KEY_PREFIX = "com.cassmall:cache:authCache:";
        private static final long AUTHENTICATION_CACHE_EXPIRE_MINUTES = 10L;
        private final RedisTemplate<String, Object> redisTemplate = new RedisTemplate();

        public IcecAuthenticationRedisCache(RedisConnectionFactory redisFactory, ObjectMapper objectMapper) {
            this.redisTemplate.setConnectionFactory(redisFactory);
            RedisSerializer<String> keySerializer = this.redisTemplate.getStringSerializer();
            this.redisTemplate.setKeySerializer(keySerializer);
            this.redisTemplate.setHashKeySerializer(keySerializer);
            Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer(Object.class);
            valueSerializer.setObjectMapper(objectMapper);
            this.redisTemplate.setValueSerializer(valueSerializer);
            this.redisTemplate.setHashValueSerializer(valueSerializer);
            this.redisTemplate.afterPropertiesSet();
        }

        public IcecAuthentication getAuthentication(String accessToken) {
            String tokenKey = this.extractTokenKey(accessToken);
            if (null == tokenKey) {
                return null;
            } else {
                tokenKey = AUTHENTICATION_CACHE_MAP_KEY_PREFIX + tokenKey;
                Object obj = this.redisTemplate.opsForValue().get(tokenKey);
                if (null != obj) {
                    this.redisTemplate.expire(tokenKey, AUTHENTICATION_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
                }

                return this.transferToReturn(obj);
            }
        }

        public void putAuthentication(String accessToken, Authentication authentication) {
            String tokenKey = this.extractTokenKey(accessToken);
            if (null != tokenKey && null != authentication) {
                tokenKey = AUTHENTICATION_CACHE_MAP_KEY_PREFIX + tokenKey;
                this.redisTemplate.opsForValue().set(tokenKey, this.transferToMap(authentication));
                this.redisTemplate.expire(tokenKey, AUTHENTICATION_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            }

        }

        protected String extractTokenKey(String value) {
            if (value == null) {
                return null;
            } else {
                if (value.indexOf(" ") > 0) {
                    value = value.split(" ")[1];
                }

                MessageDigest digest;
                try {
                    digest = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException var5) {
                    throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
                }

                try {
                    byte[] bytes = digest.digest(value.getBytes("UTF-8"));
                    return String.format("%032x", new BigInteger(1, bytes));
                } catch (UnsupportedEncodingException var4) {
                    throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
                }
            }
        }

        protected Map<String, Object> transferToMap(Authentication authentication) {
            Map<String, Object> map = new HashMap<>();
            map.put("username", authentication.getName());
            map.put("authorities", authentication.getAuthorities());
            return map;
        }

        protected IcecAuthentication transferToReturn(Object obj) {
            return null != obj && obj instanceof Map ? new IcecAuthentication((Map)obj) : null;
        }
    }

}
