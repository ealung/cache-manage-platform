package org.channel.cache.core.storage;

import lombok.Data;

/**
 * @author zhangchanglu
 * @since 2018/10/23 17:42.
 */
@Data
public class SearchParam {
    private String appName;
    private String className;
    private String cacheName;
    private String cacheKey;
    private String cacheOperation;
    private Integer model;
    private int pageIndex;
    private int pageSize;
}
