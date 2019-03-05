package org.channel.cache.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangchanglu
 * @since 2018/11/30 14:37.
 */
@Configuration
@ConfigurationProperties(prefix = "cache.manage")
public class CacheWebProperties {
    private String appName;
    private Boolean webEnable;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Boolean getWebEnable() {
        return webEnable;
    }

    public void setWebEnable(Boolean webEnable) {
        this.webEnable = webEnable;
    }
}
