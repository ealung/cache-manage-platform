package com.netease.edu.kada.cache.core.vo;

import lombok.Data;
import org.springframework.cache.interceptor.CacheOperation;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * @author zhangchanglu
 * @since 2018/10/16 11:18.
 */
@Data
public class CacheMethodVo implements Serializable {
    public CacheMethodVo() {
    }

    public CacheMethodVo(String methodName) {
        this.methodName = methodName;
    }

    private String methodName;
    private CacheOperation cacheOperation;
    private Collection<CacheManagerVo> cacheManagerDtos;

}
