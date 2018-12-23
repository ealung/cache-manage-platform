package com.netease.edu.kada.cache.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:18.
 */
@Repository
public interface CacheRepository extends CrudRepository<CacheEntity, Long> {
    Iterable<CacheEntity> findAllByAppName(String appName);

    Iterable<CacheEntity> findAllByClassNameAndAppName(String className,String appName);

    Iterable<CacheEntity> findAllByClassNameLikeAndAppName(String className,String appName);

    Iterable<CacheEntity> findAllByClassNameAndMethodNameAndAppName(String className, String methodName,String appName);

    CacheEntity findAllByClassNameAndMethodNameAndCacheConfigKeyAndAppName(String className, String methodName, String cacheConfigKey,String appName);

}
