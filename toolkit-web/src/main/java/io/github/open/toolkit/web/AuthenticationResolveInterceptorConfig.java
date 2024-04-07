package io.github.open.toolkit.web;

import io.github.open.toolkit.web.security.AuthenticationResolveInterceptor;
import io.github.open.toolkit.web.security.HttpAuthenticationExtractor;
import io.github.open.toolkit.web.security.JwtAuthenticationExtractor;
import io.github.open.toolkit.web.security.Oauth2AuthenticationExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.List;

@Configuration
@ConditionalOnMissingBean(
        name = {"authorizationEndpoint"},
        type = {"org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint"}
)
public class AuthenticationResolveInterceptorConfig {

    @Value("${icec.security.userInfoEndpointUrl}")
    private String userInfoEndpointUrl;

    @Bean
    public JwtAuthenticationExtractor jwtAuthenticationExtractor() {
        return new JwtAuthenticationExtractor();
    }

    @Bean
    public Oauth2AuthenticationExtractor oauth2AuthenticationExtractor(RedisConnectionFactory redisFactory) {
        return new Oauth2AuthenticationExtractor(redisFactory, userInfoEndpointUrl);
    }

    @Bean
    public AuthenticationResolveInterceptor authenticationResolveInterceptor(
            List<HttpAuthenticationExtractor> extractors) {
        return new AuthenticationResolveInterceptor(extractors);
    }
}
