package org.channel.cache.core.dto;

import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2018/12/30 21:52.
 */
public class CacheProjectDto {
    private String appName;
    private Collection<CacheDto> cacheDtos;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Collection<CacheDto> getCacheDtos() {
        return cacheDtos;
    }

    public void setCacheDtos(Collection<CacheDto> cacheDtos) {
        this.cacheDtos = cacheDtos;
    }
}
