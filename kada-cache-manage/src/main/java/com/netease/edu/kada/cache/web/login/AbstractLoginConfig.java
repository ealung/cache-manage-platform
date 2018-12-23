package com.netease.edu.kada.cache.web.login;

import com.netease.edu.kada.cache.core.config.CacheWebProperties;
import com.netease.edu.kada.cache.db.CacheSessionEntity;
import com.netease.edu.kada.cache.db.CacheSessionRepository;
import com.netease.edu.kada.cache.web.LoginUser;
import com.netease.edu.kada.cache.web.LoginUserConfig;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * @author zhangchanglu
 * @since 2018/11/21 19:32.
 */
@Slf4j
public class AbstractLoginConfig implements LoginUserConfig {
    private long expireTime = 18000000;
    @Resource
    private CacheSessionRepository cacheSessionRepository;
    @Resource
    private CacheWebProperties cacheWebProperties;

    @Override
    public LoginUser loginUser() {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserName("kadacache");
        loginUser.setUserPwd("kadacache");
        return loginUser;
    }

    @Override
    public void saveLoginUser(HttpServletRequest request, HttpServletResponse response, LoginServletParam loginServletParam) {
        CacheSessionEntity cacheSession = new CacheSessionEntity();
        cacheSession.setUserName(loginServletParam.getUsername());
        cacheSession.setSessionId(request.getSession().getId());
        cacheSession.setLoginTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        cacheSessionRepository.save(cacheSession);
    }

    @Override
    public LoginUser getLoginUser(HttpServletRequest request, HttpServletResponse response, LoginServletParam loginServletParam) {
        HttpSession session = request.getSession();
        CacheSessionEntity byUserNameAndSessionId = cacheSessionRepository.findByUserNameAndSessionId(loginServletParam.getUsername(), session.getId());
        if (null != byUserNameAndSessionId) {
            Long loginTime = byUserNameAndSessionId.getLoginTime();
            long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if (now - loginTime <= expireTime) {
                return loginServletParam.getLoginUserConfig().loginUser();
            } else {
                log.info("用户:{}登录已过期，需重新登录", loginServletParam.getUsername());
            }
        }
        return null;
    }

    @Override
    public boolean isLogin(HttpServletRequest request, HttpServletResponse response, LoginServletParam loginServletParam) {
        return null != getLoginUser(request, response, loginServletParam);
    }

    @Override
    public String loginPath() {
        return null;
    }

    @Override
    public boolean isEnable() {
        Boolean webEnable = cacheWebProperties.getWebEnable();
        return Objects.isNull(webEnable) ? true : webEnable;
    }
}
