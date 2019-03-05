package org.channel.cache.web;

import com.netease.edu.kada.cache.web.login.LoginServletParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangchanglu
 * @since 2018/05/17 17:49.
 */
public interface LoginUserConfig {
    LoginUser loginUser();

    void saveLoginUser(HttpServletRequest request, HttpServletResponse response, LoginServletParam loginServletParam);

    LoginUser getLoginUser(HttpServletRequest request, HttpServletResponse response, LoginServletParam loginServletParam);

    boolean isLogin(HttpServletRequest request, HttpServletResponse response, LoginServletParam loginServletParam);

    String loginPath();
    //是否开启web管理功能
    boolean isEnable();
}
