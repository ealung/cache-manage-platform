package com.netease.edu.kada.cache.core.dto;

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
public class CacheMethodDto implements Serializable{
    public CacheMethodDto() {
    }

    public CacheMethodDto(String methodName) {
        this.methodName = methodName;
    }
    private String methodName;
    private CacheOperation cacheOperation;
    private Collection<CacheManagerDto> cacheManagerDtos;

}
