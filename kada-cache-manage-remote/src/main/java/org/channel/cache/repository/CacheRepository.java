package org.channel.cache.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<CacheEntity> findAllByAppName(String appName, Pageable pageable);

    Iterable<CacheEntity> findAllByClassNameAndAppName(String className, String appName);

    Page<CacheEntity> findAllByClassNameLikeAndAppName(String className, String appName, Pageable pageable);

    CacheEntity findAllByClassNameAndMethodNameAndCacheConfigKeyAndAppName(String className, String methodName, String cacheConfigKey, String appName);

}
