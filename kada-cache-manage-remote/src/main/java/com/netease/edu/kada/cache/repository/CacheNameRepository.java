package com.netease.edu.kada.cache.repository;

import com.netease.edu.kada.cache.core.dto.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:50.
 */
@Repository

public interface CacheNameRepository extends CrudRepository<CacheNameEntity, Long> {
    Page<CacheNameEntity> findAll(Pageable pageable);

    Collection<CacheNameEntity> findByCacheNameLikeAndAppName(String cacheName, String appName);

    Collection<CacheNameEntity> findByCacheNameAndAppName(String cacheName, String appName);

    CacheNameEntity findByCacheEntity_IdAndAppName(Long cacheEntiy_id, String appName);
}
