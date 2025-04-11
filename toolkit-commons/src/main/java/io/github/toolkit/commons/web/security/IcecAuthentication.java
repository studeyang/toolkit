//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.github.toolkit.commons.web.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * 用户身份信息
 *
 * @author jian.xu
 * @version v1.2_20200519
 */
public class IcecAuthentication implements Authentication {

    private static final long serialVersionUID = 1464683110413264876L;

    public static final String USERNAME_KEY = "username";
    public static final String AUTHORITIES_KEY = "authorities";
    private UserDetails userDetails;
    private final Collection<GrantedAuthority> authorities;
    private boolean authenticated;

    private static Map<String, Object> toMap(String username, List<String> roles) {
        Map<String, Object> map = new HashMap<>();
        map.put(USERNAME_KEY, username);
        map.put(AUTHORITIES_KEY, roles);
        return map;
    }

    public IcecAuthentication(Map<String, Object> map) {
        this.authenticated = false;
        if (!map.containsKey(USERNAME_KEY)) {
            throw new IllegalArgumentException("cannot find '" + USERNAME_KEY + "'!");
        } else {

            boolean enabled = getBoolean(map, "enabled", true);
            boolean accountNonExpired = getBoolean(map, "accountNonExpired", true);
            boolean credentialsNonExpired = getBoolean(map, "credentialsNonExpired", true);
            boolean accountNonLocked = getBoolean(map, "accountNonLocked", true);

            List<GrantedAuthority> mapAuthorities = this.extractAuthorities(map);
            if (null != mapAuthorities && mapAuthorities.size() > 0) {
                ArrayList<GrantedAuthority> temp = new ArrayList(mapAuthorities.size());
                temp.addAll(mapAuthorities);
                this.authorities = Collections.unmodifiableList(temp);
            } else {
                this.authorities = AuthorityUtils.NO_AUTHORITIES;
            }

            User user = new User(map.get(USERNAME_KEY).toString(), "******",
                    enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                    this.authorities);

            this.userDetails = user;
            this.setAuthenticated(this.authorities.size() > 0);
        }
    }

    private boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        if (!map.containsKey(key)) {
            return defaultValue;
        }

        try {
            return Boolean.parseBoolean(map.get(key).toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
        if (map.containsKey(AUTHORITIES_KEY)) {
            Object value = map.get(AUTHORITIES_KEY);
            if (value instanceof List) {
                return extractAuthorities((List)value);
            }
        }

        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    private List<GrantedAuthority> extractAuthorities(List list) {
        if (list.size() > 0) {
            List<GrantedAuthority> tmpAuthorities = new ArrayList<>();
            Iterator var4 = list.iterator();

            while (var4.hasNext()) {
                Object obj = var4.next();
                String role = null;
                if (obj instanceof String) {
                    role = obj.toString();
                } else if (obj instanceof Map) {
                    try {
                        role = (String) ((Map) obj).get("authority");
                    } catch (Exception var8) {
                        ;
                    }
                }

                if (null != role && role.length() > 0) {
                    tmpAuthorities.add(new SimpleGrantedAuthority(role));
                }
            }

            return tmpAuthorities;
        } else {
            return null;
        }
    }


    public String getCredentials() {
        return this.userDetails.getPassword();
    }

    public UserDetails getPrincipal() {
        return this.userDetails;
    }

    public String getName() {
        return this.userDetails.getUsername();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public UserDetails getDetails() {
        return this.userDetails;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }


}
