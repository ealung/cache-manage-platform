package org.channel.cache.db;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.channel.cache.core.config.CacheWebProperties;
import org.channel.cache.core.dto.CacheDto;
import org.channel.cache.core.dto.CacheNameDto;
import org.channel.cache.core.dto.CacheProjectDto;
import org.channel.cache.core.dto.PageInfo;
import org.channel.cache.core.storage.CacheStorage;
import org.channel.cache.core.storage.ProjectCacheInvoke;
import org.channel.cache.core.storage.SearchParam;
import org.channel.cache.core.vo.CacheKeyVo;
import org.channel.cache.core.vo.CacheManagerVo;
import org.channel.cache.core.vo.CacheMethodVo;
import org.channel.cache.core.vo.ClassCacheVo;
import org.channel.cache.core.utils.PageToPageInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:39.
 */
@ConditionalOnMissingBean(CacheStorage.class)
@Slf4j
public class DbLocalCacheStorage implements CacheStorage, ProjectCacheInvoke {
    @Resource
    private CacheRepository cacheRepository;
    @Resource
    private CacheNameRepository cacheNameRepository;
    @Resource
    private CacheKeyRepository cacheKeyRepository;
    @Resource
    private CacheWebProperties cacheWebProperties;
    @Resource
    private CacheManager cacheManager;

    @Override
    public Collection<String> getAllProject() {
        return null;
    }

    @Override
    public PageInfo<ClassCacheVo> search(SearchParam searchParam, String appName, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<CacheEntity> allByClassNameLikeAndAppName = Page.empty(pageable);
        //cacheName视图模式
        if (!Objects.isNull(searchParam.getModel()) && searchParam.getModel().equals(2)) {
            return searchForCacheName(searchParam, appName, pageable);
        }
        //类视图模式
        Multimap<String, CacheMethodVo> classCache = ArrayListMultimap.create();
        if (!Strings.isNullOrEmpty(searchParam.getClassName())) {
            allByClassNameLikeAndAppName = cacheRepository.findAllByClassNameLikeAndAppName(getLike(searchParam.getClassName()), appName, pageable);
            allByClassNameLikeAndAppName.forEach(cacheEntity -> {
                CacheMethodVo cacheMethodDto = getCacheMethod(cacheEntity, appName);
                classCache.put(cacheEntity.getClassName(), cacheMethodDto);
            });
        } else if (!Strings.isNullOrEmpty(searchParam.getCacheName())) {
            Collection<CacheNameEntity> byCacheNameLike = cacheNameRepository.findByCacheNameLikeAndAppName(getLike(searchParam.getCacheName()), appName);
            byCacheNameLike.forEach(cacheNameEntity -> {
                CacheEntity cacheEntity = cacheNameEntity.getCacheEntity();
                CacheMethodVo cacheMethodDto = getCacheMethod(cacheEntity, appName);
                classCache.put(cacheEntity.getClassName(), cacheMethodDto);
            });
        } else if (!Strings.isNullOrEmpty(searchParam.getCacheKey())) {
            Iterable<CacheKeyEntity> byKeyLike = cacheKeyRepository.findByCacheKeyLikeAndAppName(getLike(searchParam.getCacheKey()), appName);
            byKeyLike.forEach(cacheKeyEntity -> {
                CacheEntity cacheEntity = cacheKeyEntity.getCacheEntity();
                CacheMethodVo cacheMethodDto = getCacheMethod(cacheEntity, appName);
                classCache.put(cacheEntity.getClassName(), cacheMethodDto);
            });
        } else {
            return getAllCache(appName, pageIndex, pageSize);
        }
        return PageToPageInfoUtils.convertTOPageInfo(getClassCache(classCache.asMap()), allByClassNameLikeAndAppName);
    }

    /**
     * cacheName视图，className存放cacheName
     *
     * @param searchParam 查询条件
     * @return 查询结果
     */
    private PageInfo<ClassCacheVo> searchForCacheName(SearchParam searchParam, String appName, Pageable pageable) {
        Multimap<String, CacheMethodVo> classCache = ArrayListMultimap.create();
        Collection<CacheNameEntity> all = new ArrayList<>();
        Page<CacheEntity> page = Page.empty(pageable);
        if (!Strings.isNullOrEmpty(searchParam.getClassName())) {
            page = cacheRepository.findAllByClassNameLikeAndAppName(getLike(searchParam.getClassName()), appName, pageable);
            Iterable<CacheEntity> allByClassName = page;
            for (CacheEntity cacheEntity : allByClassName) {
                all.addAll(cacheNameRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName));
            }
        } else if (!Strings.isNullOrEmpty(searchParam.getCacheName())) {
            all = cacheNameRepository.findByCacheNameLikeAndAppName(getLike(searchParam.getCacheName()), appName);
        } else if (!Strings.isNullOrEmpty(searchParam.getCacheKey())) {
            Iterable<CacheKeyEntity> byKeyLike = cacheKeyRepository.findByCacheKeyLikeAndAppName(getLike(searchParam.getCacheKey()), appName);
            for (CacheKeyEntity cacheKeyEntity : byKeyLike) {
                CacheEntity cacheEntity = cacheKeyEntity.getCacheEntity();
                all.addAll(cacheNameRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName));
            }
        } else {
            for (CacheNameEntity cacheNameEntity : cacheNameRepository.findAll()) {
                all.add(cacheNameEntity);
            }
        }
        for (CacheNameEntity cacheNameEntity : all) {
            CacheEntity cacheEntity = cacheNameEntity.getCacheEntity();
            CacheMethodVo cacheMethodDto = getCacheMethod(cacheEntity, appName);
            classCache.put(cacheNameEntity.getCacheName(), cacheMethodDto);
        }
        return PageToPageInfoUtils.convertTOPageInfo(getClassCache(classCache.asMap()), page);
    }

    private String getLike(String param) {
        return "%" + param + "%";
    }

    private Collection<ClassCacheVo> getClassCache(Map<String, Collection<CacheMethodVo>> map) {
        Collection<ClassCacheVo> classCacheDtos = new ArrayList<>();
        map.forEach((s, cacheMethodDtos) -> {
            ClassCacheVo classCacheDto = new ClassCacheVo(cacheMethodDtos);
            classCacheDto.setClassName(s);
            classCacheDtos.add(classCacheDto);
        });
        return classCacheDtos;
    }

    private CacheMethodVo getCacheMethod(CacheEntity cacheEntity, String appName) {
        CacheMethodVo cacheMethodDto = new CacheMethodVo();
        cacheMethodDto.setMethodName(cacheEntity.getMethodName());
        cacheMethodDto.setCacheManagerDtos(cacheManagerDtos(cacheEntity, appName));
        return cacheMethodDto;
    }

    @Override
    public void allCacheConfig(CacheProjectDto cacheProjectDto) {
        String appName = cacheProjectDto.getAppName();
        if (cacheWebProperties.getWebEnable()) {
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
    }

    @Override
    public PageInfo<ClassCacheVo> getAllCache(String appName, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Multimap<String, CacheMethodVo> classCache = ArrayListMultimap.create();
        Page<CacheEntity> allByAppName = cacheRepository.findAllByAppName(appName, pageable);
        for (CacheEntity cacheEntity : allByAppName) {
            CacheMethodVo cacheMethodDto = new CacheMethodVo();
            cacheMethodDto.setMethodName(cacheEntity.getMethodName());
            cacheMethodDto.setCacheManagerDtos(cacheManagerDtos(cacheEntity, appName));
            classCache.put(cacheEntity.getClassName(), cacheMethodDto);
        }
        return PageToPageInfoUtils.convertTOPageInfo(getClassCache(classCache.asMap()), allByAppName);
    }

    private Collection<CacheManagerVo> cacheManagerDtos(CacheEntity cacheEntity, String appName) {
        Collection<CacheManagerVo> cacheManagerDtos = new ArrayList<>();
        CacheManagerVo cacheManagerDto = new CacheManagerVo();
        cacheManagerDto.setCacheConfigKey(cacheEntity.getCacheConfigKey());
        cacheManagerDto.setCacheOperation(cacheEntity.getCacheOperation());
        cacheManagerDto.setClassName(cacheEntity.getClassName());
        cacheManagerDto.setMethodName(cacheEntity.getMethodName());
        Collection<CacheKeyEntity> byCacheEntity_id = cacheKeyRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName);
        if (!CollectionUtils.isEmpty(byCacheEntity_id)) {
            List<String> collect = byCacheEntity_id.stream().map(CacheKeyEntity::getCacheKey).collect(Collectors.toList());
            cacheManagerDto.setCacheKeys(collect);
        }
        cacheManagerDtos.add(cacheManagerDto);
        return cacheManagerDtos;
    }

    @Override
    public Collection<CacheKeyVo> findCacheKeys(String cacheName, String appName) {
        Collection<CacheKeyVo> allKey = new ArrayList<>();
        Collection<CacheNameEntity> byCacheName = cacheNameRepository.findByCacheNameAndAppName(cacheName, appName);
        byCacheName.forEach(cacheNameEntity -> {
            Collection<CacheKeyEntity> byCacheEntity_idAndAppName = cacheKeyRepository.findByCacheEntity_IdAndAppName(cacheNameEntity.getCacheEntity().getId(), appName);
            for (CacheKeyEntity cacheKeyEntity : byCacheEntity_idAndAppName) {
                CacheKeyVo cacheKeyEntityDto = new CacheKeyVo();
                BeanUtils.copyProperties(cacheKeyEntity, cacheKeyEntityDto);
                allKey.add(cacheKeyEntityDto);
            }
        });
        return allKey;
    }

    @Override
    public boolean removeCacheName(String cacheName, String key, String appName) {
        cacheNameRepository.findByCacheNameAndAppName(cacheName, appName).forEach(cacheNameEntity -> {
            if (Strings.isNullOrEmpty(key)) {
                for (CacheKeyEntity cacheKeyEntity : cacheKeyRepository.findByCacheEntity_IdAndAppName(cacheNameEntity.getCacheEntity().getId(), appName)) {
                    cacheKeyRepository.delete(cacheKeyEntity);
                }
            } else {
                cacheKeyRepository.removeByCacheEntity_IdAndCacheKeyAndAppName(cacheNameEntity.getCacheEntity().getId(), key, appName);
            }
        });
        Cache cache = cacheManager.getCache(cacheName);
        if (!Objects.isNull(cache)) {
            if (!Strings.isNullOrEmpty(key)) {
                cache.evict(key);
            } else {
                cache.clear();
            }
        }
        return true;
    }

    @Override
    public boolean removeClassName(String className, String key, String appName) {
        cacheRepository.findAllByClassNameAndAppName(className, appName).forEach(cacheEntity -> {
            Collection<CacheNameEntity> byCacheEntity_idAndAppName = cacheNameRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName);
            for (CacheNameEntity cacheNameEntity : byCacheEntity_idAndAppName) {
                Cache cache = cacheManager.getCache(cacheNameEntity.getCacheName());
                if (Strings.isNullOrEmpty(key)) {
                    for (CacheKeyEntity cacheKeyEntity : cacheKeyRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName)) {
                        cacheKeyRepository.delete(cacheKeyEntity);
                        if (!Objects.isNull(cache)) {
                            cache.evict(cacheKeyEntity.getCacheKey());
                        }
                    }
                } else {
                    cacheKeyRepository.removeByCacheEntity_IdAndCacheKeyAndAppName(cacheEntity.getId(), key, appName);
                    if (!Objects.isNull(cache)) {
                        cache.evict(key);
                    }
                }
            }
        });
        return true;
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
        for (CacheKeyEntity cacheKeyEntity : cacheKeyRepository.findByCacheKeyAndAppName(key, appName)) {
            for (CacheNameEntity cacheNameEntity : cacheNameRepository.findByCacheEntity_IdAndAppName(cacheKeyEntity.getCacheEntity().getId(), appName)) {
                Cache cache = cacheManager.getCache(cacheNameEntity.getCacheName());
                if (!Objects.isNull(cache)) {
                    cache.evict(key);
                }
            }
        }
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

    private void saveCacheKeyEntity(String key, CacheEntity cacheEntity, String appName) {
        CacheKeyEntity cacheKeyEntity = new CacheKeyEntity();
        cacheKeyEntity.setCacheKey(key);
        cacheKeyEntity.setAppName(appName);
        cacheKeyEntity.setCacheEntity(cacheEntity);
        cacheKeyRepository.save(cacheKeyEntity);
    }

    @Override
    public void afterCacheGet(String cacheName, String key, String appName) {

    }
}
