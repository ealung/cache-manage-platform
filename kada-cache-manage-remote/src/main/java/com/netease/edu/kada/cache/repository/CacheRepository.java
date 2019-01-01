package com.netease.edu.kada.cache.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:18.
 */
@Repository
public interface CacheRepository extends CrudRepository<CacheEntity, Long> {
    @Query("select t.appName as goods from CacheEntity t group by t.appName")
    Collection<String> findAllGroupByAppName();

    Iterable<CacheEntity> findAllByAppName(String appName);

    Iterable<CacheEntity> findAllByClassNameAndAppName(String className, String appName);

    Iterable<CacheEntity> findAllByClassNameLikeAndAppName(String className, String appName);

    Iterable<CacheEntity> findAllByClassNameAndMethodNameAndAppName(String className, String methodName, String appName);

    CacheEntity findAllByClassNameAndMethodNameAndCacheConfigKeyAndAppName(String className, String methodName, String cacheConfigKey, String appName);

}
