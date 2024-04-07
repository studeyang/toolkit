package io.github.open.toolkit.web.security;

import org.springframework.security.core.Authentication;

public interface AuthenticationCache<T extends Authentication> {
	
	public T getAuthentication(String accessToken);
	
	public void putAuthentication(String accessToken, Authentication authentication);

}
