package org.channel.cache.web.login;

import com.google.common.base.Strings;
import com.netease.edu.kada.cache.web.LoginUserConfig;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhangchanglu
 * @since 2018/10/26 10:15.
 */
public class LoginFilter implements Filter {
    protected LoginServletParam loginServletParam;

    public LoginFilter(LoginServletParam loginServletParam) {
        this.loginServletParam = loginServletParam;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();

        response.setCharacterEncoding("utf-8");

        if (contextPath == null) { // root context
            contextPath = "";
        }
        String uri = contextPath + servletPath;
        String path = requestURI.substring(loginServletParam.getPrefix().length());
        LoginUserConfig loginUserConfig = loginServletParam.getLoginUserConfig();
        if (!loginUserConfig.isEnable() && !isIgnore(path)) {
            response.sendRedirect(loginServletParam.getPrefix() + "/error.html");
            return;
        }
        if (loginServletParam.getLoginUrl().equals(path)) {
            String usernameParam = request.getParameter(loginServletParam.getParamUserName());
            String passwordParam = request.getParameter(loginServletParam.getParamUserPassword());
            if (loginServletParam.getUsername().equals(usernameParam) && loginServletParam.getPassword().equals(passwordParam)) {
                loginUserConfig.saveLoginUser(request, response, loginServletParam);
                response.getWriter().print("success");
            } else {
                response.getWriter().print("error");
            }
            return;
        }
        if (!loginUserConfig.isLogin(request, response, loginServletParam)
                && !isIgnore(path)) {
            String loginUrl = loginUserConfig.loginPath();
            loginUrl = Strings.isNullOrEmpty(loginUrl) ? loginServletParam.getPrefix() + loginServletParam.getLoginPage() : loginUrl;
            response.sendRedirect(loginUrl);
            return;
        }
        boolean b = returnResourceFile(path, uri, response);
        if (!b) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean isIgnore(String path) {
        return loginServletParam.getLoginPage().equals(path) //
                || path.startsWith("/css")//
                || path.startsWith("/js") //
                || path.startsWith("/img")
                || path.startsWith("/error");
    }

    @Override
    public void destroy() {

    }

    protected boolean returnResourceFile(String fileName, String uri, HttpServletResponse response)
            throws ServletException,
            IOException {
        String filePath = getFilePath(fileName);
        if (fileName.endsWith(".jpg")) {
            byte[] bytes = Utils.readByteArrayFromResource(filePath);
            if (bytes != null) {
                response.getOutputStream().write(bytes);
            }
            return true;
        }
        String text = Utils.readFromResource(filePath);
        if (text == null) {
            return false;
        }
        if (fileName.endsWith(".css")) {
            response.setContentType("text/css;charset=utf-8");
        } else if (fileName.endsWith(".js")) {
            response.setContentType("text/javascript;charset=utf-8");
        }
        response.getWriter().write(text);
        return true;
    }

    protected String getFilePath(String fileName) {
        return loginServletParam.getResourcePath() + fileName;
    }

}
