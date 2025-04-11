package io.github.toolkit.cache.servlet;

import io.github.toolkit.cache.util.IOUtils;
import io.github.toolkit.cache.util.IPAddress;
import io.github.toolkit.cache.util.IPRange;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ResourceServlet extends HttpServlet {
    private static final long serialVersionUID = 9123905057760862321L;
    protected static final Logger LOG = LoggerFactory.getLogger(ResourceServlet.class);
    public static final String SESSION_USER_KEY = "cache-user";
    public static final String PARAM_NAME_USERNAME = "loginUsername";
    public static final String PARAM_NAME_PASSWORD = "loginPassword";
    public static final String PARAM_NAME_ALLOW = "allow";
    public static final String PARAM_NAME_DENY = "deny";
    public static final String PARAM_REMOTE_ADDRESS = "remoteAddress";
    protected String username = null;
    protected String password = null;
    protected List<IPRange> allowList = new ArrayList<>();
    protected List<IPRange> denyList = new ArrayList<>();
    protected String resourcePath;
    protected String paramRemoteAddress = null;

    protected ResourceServlet(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public void init() {
        this.initAuthEnv();
    }

    private void initAuthEnv() {
        this.username = getInitParameter(PARAM_NAME_USERNAME);
        this.password = getInitParameter(PARAM_NAME_PASSWORD);
        this.paramRemoteAddress = getInitParameter(PARAM_REMOTE_ADDRESS);

        try {
            String allowValue = this.getInitParameter(PARAM_NAME_ALLOW);
            if (StringUtils.isNotBlank(allowValue)) {
                String[] allowItems = allowValue.trim().split(",");
                this.allowList = Arrays.stream(allowItems)
                        .filter(StringUtils::isNotBlank)
                        .map(IPRange::new)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            String msg = "initParameter config error, allow : " + this.getInitParameter(PARAM_NAME_ALLOW);
            LOG.error(msg, e);
        }

        try {
            String denyValue = this.getInitParameter(PARAM_NAME_DENY);
            if (StringUtils.isNotBlank(denyValue)) {
                String[] denyValues = denyValue.trim().split(",");
                this.denyList = Arrays.stream(denyValues)
                        .filter(StringUtils::isNotBlank)
                        .map(IPRange::new)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            String msg = "initParameter config error, deny : " + this.getInitParameter(PARAM_NAME_DENY);
            LOG.error(msg, e);
        }

    }

    public boolean isPermittedRequest(String remoteAddress) {
        boolean ipV6 = remoteAddress != null && remoteAddress.contains(":");
        if (ipV6) {
            return "0:0:0:0:0:0:0:1".equals(remoteAddress) || this.denyList.isEmpty() && this.allowList.isEmpty();
        }

        IPAddress ipAddress = new IPAddress(remoteAddress);
        for (IPRange ipRange : allowList) {
            if (ipRange.isIPAddressInRange(ipAddress)) {
                return true;
            }
        }
        for (IPRange ipRange : denyList) {
            if (ipRange.isIPAddressInRange(ipAddress)) {
                return false;
            }
        }
        return false;
    }

    protected String getFilePath(String fileName) {
        return this.resourcePath + fileName;
    }

    protected void returnResourceFile(String fileName, String uri, HttpServletResponse response) throws IOException {
        String filePath = this.getFilePath(fileName);
        if (filePath.endsWith(".html")) {
            response.setContentType("text/html; charset=utf-8");

        } else if (fileName.endsWith(".jpg")) {
            byte[] bytes = IOUtils.readByteArrayFromResource(filePath);
            if (bytes != null) {
                response.getOutputStream().write(bytes);
            }

        } else {
            String text = IOUtils.readFromResource(filePath);
            if (text == null) {
                response.sendRedirect(uri + "/index.html");
            } else {
                if (fileName.endsWith(".css")) {
                    response.setContentType("text/css;charset=utf-8");
                } else if (fileName.endsWith(".js")) {
                    response.setContentType("text/javascript;charset=utf-8");
                }
                response.getWriter().write(text);
            }
        }
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
        String servletPath = request.getServletPath();
        String requestUri = request.getRequestURI();

        response.setCharacterEncoding("utf-8");

        String uri = contextPath + servletPath;
        String path = requestUri.substring(contextPath.length() + servletPath.length());
        this.returnResourceFile(path, uri, response);
    }

    public boolean containsUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(SESSION_USER_KEY) != null;
    }

    public boolean checkLoginParam(HttpServletRequest request) {
        String usernameParam = request.getParameter(PARAM_NAME_USERNAME);
        String passwordParam = request.getParameter(PARAM_NAME_PASSWORD);
        if (null != this.username && null != this.password) {
            return this.username.equals(usernameParam) && this.password.equals(passwordParam);
        } else {
            return false;
        }
    }

    public boolean isRequireAuth() {
        return this.username != null;
    }

    public boolean isPermittedRequest(HttpServletRequest request) {
        String remoteAddress = getRemoteAddress(request);
        return isPermittedRequest(remoteAddress);
    }

    protected String getRemoteAddress(HttpServletRequest request) {
        String remoteAddress = null;
        if (this.paramRemoteAddress != null) {
            remoteAddress = request.getHeader(this.paramRemoteAddress);
        }

        if (remoteAddress == null) {
            remoteAddress = request.getRemoteAddr();
        }

        return remoteAddress;
    }

    protected abstract String process(String url);
}