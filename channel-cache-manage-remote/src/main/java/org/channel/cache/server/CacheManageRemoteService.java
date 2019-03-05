package org.channel.cache.server;

import org.channel.cache.core.dto.CacheDto;
import org.channel.cache.core.dto.CacheNameDto;
import org.channel.cache.core.dto.CacheProjectDto;
import org.channel.cache.core.storage.ProjectCacheInvoke;
import lombok.extern.slf4j.Slf4j;
import org.channel.cache.repository.*;
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
@Component("cacheManageRemoteService")
public class CacheManageRemoteService implements ProjectCacheInvoke {
    @Resource
    private CacheRepository cacheRepository;
    @Resource
    private CacheNameRepository cacheNameRepository;
    @Resource
    private CacheKeyRepository cacheKeyRepository;

    @Override
    public void allCacheConfig(CacheProjectDto cacheProjectDto) {
        String appName = cacheProjectDto.getAppName();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Collection<CacheDto> cacheDtos = cacheProjectDto.getCacheDtos();
                for (CacheDto cacheDto : cacheDtos) {
                    CacheEntity cacheEntity = new CacheEntity();
                    cacheEntity.setAppName(appName);
                    cacheEntity.setClassName(cacheDto.getClassName());
                    cacheEntity.setMethodName(cacheDto.getMethodName());
                    cacheEntity.setCacheConfigKey(cacheDto.getCacheConfigKey());
                    cacheEntity.setCacheOperation(cacheDto.getCacheOperation());
                    CacheEntity allByClassNameAndMethodNameAndCacheConfigKey = cacheRepository.findAllByClassNameAndMethodNameAndCacheConfigKeyAndAppName(cacheDto.getClassName(), cacheDto.getMethodName(), cacheDto.getCacheConfigKey(), appName);
                    if (!Objects.isNull(allByClassNameAndMethodNameAndCacheConfigKey)) {
                        continue;
                    }
                    cacheRepository.save(cacheEntity);
                    for (CacheNameDto cacheNameDto : cacheDto.getCacheNameDtos()) {
                        CacheNameEntity cacheNameEntity = new CacheNameEntity();
                        cacheNameEntity.setCacheName(cacheNameDto.getCacheName());
                        cacheNameEntity.setAppName(appName);
                        cacheNameEntity.setCacheEntity(cacheEntity);
                        cacheNameRepository.save(cacheNameEntity);
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
    public synchronized void afterCacheEvict(String cacheName, String key, String appName) {
        cacheKeyRepository.removeByCacheKeyAndAppName(key, appName);
    }

    @Override
    public void afterCachePut(String cacheName, String key, String className, String methodName, String cacheConfigKey, String appName) {
        CacheEntity cacheEntity = cacheRepository.findAllByClassNameAndMethodNameAndCacheConfigKeyAndAppName(className, methodName, cacheConfigKey, appName);
        if (null != cacheEntity) {
            Collection<CacheKeyEntity> byCacheKeyAndCacheEntity_idAndAppName = cacheKeyRepository.findByCacheKeyAndCacheEntity_IdAndAppName(key, cacheEntity.getId(), appName);
            if (CollectionUtils.isEmpty(byCacheKeyAndCacheEntity_idAndAppName)) {
                saveCacheKeyEntity(key, cacheEntity, appName);
            } else if (byCacheKeyAndCacheEntity_idAndAppName.size() > 1) {
                //这里有可能部分key过期没能同步删除，造成多次缓存，暂时先清空再增加
                log.warn("repetition cacheId:{} - key :{}", cacheEntity.getId(), key);
                for (CacheKeyEntity keyEntity : byCacheKeyAndCacheEntity_idAndAppName) {
                    cacheKeyRepository.delete(keyEntity);
                }
                saveCacheKeyEntity(key, cacheEntity, appName);
            }
        }
    }

    @Override
    public void afterCacheGet(String cacheName, String key, String appName) {

    }

    private void saveCacheKeyEntity(String key, CacheEntity cacheEntity, String appName) {
        CacheKeyEntity cacheKeyEntity = new CacheKeyEntity();
        cacheKeyEntity.setCacheKey(key);
        cacheKeyEntity.setAppName(appName);
        cacheKeyEntity.setCacheEntity(cacheEntity);
        cacheKeyRepository.save(cacheKeyEntity);
    }
}
