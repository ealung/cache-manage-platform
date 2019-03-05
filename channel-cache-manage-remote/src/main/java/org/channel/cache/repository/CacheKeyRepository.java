package org.channel.cache.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:54.
 */
@Repository
public interface CacheKeyRepository extends CrudRepository<CacheKeyEntity, Long> {
    Collection<CacheKeyEntity> findByCacheKeyAndCacheEntity_IdAndAppName(String key, Long cacheEntityId, String appName);

    Iterable<CacheKeyEntity> findByCacheKeyAndAppName(String key, String appName);

    Iterable<CacheKeyEntity> findByCacheKeyLikeAndAppName(String key, String appName);
    @Transactional
    void removeByCacheEntity_IdAndCacheKeyAndAppName(Long cacheEntityId, String key, String appName);

    Collection<CacheKeyEntity> findByCacheEntity_IdAndAppName(Long cacheEntityId, String appName);
    @Transactional
    int removeByCacheKeyAndAppName(String key, String appName);
}
