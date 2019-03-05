package org.channel.cache.dto;

import lombok.Data;

/**
 * @author zhangchanglu
 * @since 2018/12/23 00:58.
 */
@Data
public class CacheManageDto {
    String cacheName;
    String appName;
    Object key;
    String className;
    String methodName;
    String cacheConfigKey;
    //Collection<ClassCacheDto> cacheConfig;
}
