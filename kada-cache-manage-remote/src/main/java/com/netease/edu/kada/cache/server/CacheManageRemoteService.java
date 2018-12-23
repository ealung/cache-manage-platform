package com.netease.edu.kada.cache.server;

import com.netease.edu.kada.cache.core.dto.CacheMethodDto;
import com.netease.edu.kada.cache.core.dto.ClassCacheDto;
import com.netease.edu.kada.cache.core.storage.ProjectCacheInvoke;
import com.netease.edu.kada.cache.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Objects;

/**
 * @author zhangchanglu
 * @since 2018/12/22 22:42.
 */
@Slf4j
@Component
public class CacheManageRemoteService implements ProjectCacheInvoke {
    @Resource
    private CacheRepository cacheRepository;
    @Resource
    private CacheNameRepository cacheNameRepository;
    @Resource
    private CacheKeyRepository cacheKeyRepository;

    @Override
    public void allCacheConfig(Collection<ClassCacheDto> cacheConfig, String appName) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (ClassCacheDto classCacheDto : cacheConfig) {
                    log.info("load cache class {}", classCacheDto.getClassName());
                    for (CacheMethodDto cacheMethodDto : classCacheDto.getCacheMethodDtos()) {
                        CacheOperation cacheOperation = cacheMethodDto.getCacheOperation();
                        CacheEntity cacheEntity = new CacheEntity();
                        cacheEntity.setAppName(appName);
                        cacheEntity.setClassName(classCacheDto.getClassName());
                        cacheEntity.setMethodName(cacheMethodDto.getMethodName());
                        cacheEntity.setCacheConfigKey(cacheOperation.getKey());
                        cacheEntity.setCacheOperation(cacheOperation.getClass().getSimpleName());
                        CacheEntity allByClassNameAndMethodNameAndCacheConfigKey = cacheRepository.findAllByClassNameAndMethodNameAndCacheConfigKeyAndAppName(classCacheDto.getClassName(), cacheMethodDto.getMethodName(), cacheOperation.getKey(), appName);
                        if (!Objects.isNull(allByClassNameAndMethodNameAndCacheConfigKey)) {
                            continue;
                        }
                        cacheRepository.save(cacheEntity);
                        for (String s : cacheOperation.getCacheNames()) {
                            CacheNameEntity cacheNameEntity = new CacheNameEntity();
                            cacheNameEntity.setCacheName(s);
                            cacheNameEntity.setAppName(appName);
                            cacheNameEntity.setCacheEntity(cacheEntity);
                            cacheNameRepository.save(cacheNameEntity);
                        }
                    }
                }
                log.info("load cache class finish");
            }
        });
        thread.start();
    }

    @Override
    public void afterClear(String cacheName, String appName) {
        cacheNameRepository.findByCacheNameAndAppName(cacheName, appName).forEach(cacheNameEntity -> {
            for (CacheKeyEntity cacheKeyEntity : cacheKeyRepository.findByCacheEntity_IdAndAppName(cacheNameEntity.getCacheEntity().getId(), appName)) {
                cacheKeyRepository.delete(cacheKeyEntity);
            }
        });
    }

    @Override
    @Transactional
    public synchronized void afterCacheEvict(String cacheName, Object key, String appName) {
        cacheKeyRepository.removeByCacheKeyAndAppName(key.toString(), appName);
    }

    @Override
    public void afterCachePut(String cacheName, Object key, String className, String methodName, String cacheConfigKey, String appName) {
        CacheEntity cacheEntity = cacheRepository.findAllByClassNameAndMethodNameAndCacheConfigKeyAndAppName(className, methodName, cacheConfigKey, appName);
        if (null != cacheEntity) {
            Collection<CacheKeyEntity> byCacheKeyAndCacheEntity_idAndAppName = cacheKeyRepository.findByCacheKeyAndCacheEntity_IdAndAppName(key.toString(), cacheEntity.getId(), appName);
            if (CollectionUtils.isEmpty(byCacheKeyAndCacheEntity_idAndAppName)) {
                saveCacheKeyEntity(key.toString(), cacheEntity, appName);
            } else if (byCacheKeyAndCacheEntity_idAndAppName.size() > 1) {
                //这里有可能部分key过期没能同步删除，造成多次缓存，暂时先清空再增加
                log.warn("repetition cacheId:{} - key :{}", cacheEntity.getId(), key);
                for (CacheKeyEntity keyEntity : byCacheKeyAndCacheEntity_idAndAppName) {
                    cacheKeyRepository.delete(keyEntity);
                }
                saveCacheKeyEntity(key.toString(), cacheEntity, appName);
            }
        }
    }

    @Override
    public void afterCacheGet(String cacheName, Object key, String appName) {

    }

    private void saveCacheKeyEntity(String key, CacheEntity cacheEntity, String appName) {
        CacheKeyEntity cacheKeyEntity = new CacheKeyEntity();
        cacheKeyEntity.setCacheKey(key);
        cacheKeyEntity.setAppName(appName);
        cacheKeyEntity.setCacheEntity(cacheEntity);
        cacheKeyRepository.save(cacheKeyEntity);
    }
}
