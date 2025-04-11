package io.github.toolkit.web.security;

import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface HttpAuthenticationExtractor extends Ordered {

    boolean supportRequest(HttpServletRequest request);

    Authentication extractAuthentication(HttpServletRequest request);
}
