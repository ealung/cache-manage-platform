package com.netease.edu.kada.cache.dto;

import com.netease.edu.kada.cache.core.dto.*;
import lombok.Data;

import java.util.Collection;

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
