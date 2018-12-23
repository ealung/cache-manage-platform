package com.netease.edu.kada.cache.repository;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.netease.edu.kada.cache.client.CacheManageClientService;
import com.netease.edu.kada.cache.core.dto.CacheKeyEntityDto;
import com.netease.edu.kada.cache.core.dto.CacheManagerDto;
import com.netease.edu.kada.cache.core.dto.CacheMethodDto;
import com.netease.edu.kada.cache.core.dto.ClassCacheDto;
import com.netease.edu.kada.cache.core.storage.CacheStorage;
import com.netease.edu.kada.cache.core.storage.SearchParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:39.
 */
@Slf4j
@Repository
public class DbLocalCacheStorage implements CacheStorage {
    @Resource
    private CacheRepository cacheRepository;
    @Resource
    private CacheNameRepository cacheNameRepository;
    @Resource
    private CacheKeyRepository cacheKeyRepository;
    @Resource
    private CacheManageClientService cacheManageClientService;

    @Override
    public Collection<ClassCacheDto> search(SearchParam searchParam, String appName) {
        //cacheName视图模式
        if (!Objects.isNull(searchParam.getModel()) && searchParam.getModel().equals(2)) {
            return searchForCacheName(searchParam, appName);
        }
        //类视图模式
        Multimap<String, CacheMethodDto> classCache = ArrayListMultimap.create();
        if (!Strings.isNullOrEmpty(searchParam.getClassName())) {
            Iterable<CacheEntity> allByClassName = cacheRepository.findAllByClassNameLikeAndAppName(getLike(searchParam.getClassName()), appName);
            allByClassName.forEach(cacheEntity -> {
                CacheMethodDto cacheMethodDto = getCacheMethod(cacheEntity, appName);
                classCache.put(cacheEntity.getClassName(), cacheMethodDto);
            });
        } else if (!Strings.isNullOrEmpty(searchParam.getCacheName())) {
            Collection<CacheNameEntity> byCacheNameLike = cacheNameRepository.findByCacheNameLikeAndAppName(getLike(searchParam.getCacheName()), appName);
            byCacheNameLike.forEach(cacheNameEntity -> {
                CacheEntity cacheEntity = cacheNameEntity.getCacheEntity();
                CacheMethodDto cacheMethodDto = getCacheMethod(cacheEntity, appName);
                classCache.put(cacheEntity.getClassName(), cacheMethodDto);
            });
        } else if (!Strings.isNullOrEmpty(searchParam.getCacheKey())) {
            Iterable<CacheKeyEntity> byKeyLike = cacheKeyRepository.findByCacheKeyLikeAndAppName(getLike(searchParam.getCacheKey()), appName);
            byKeyLike.forEach(cacheKeyEntity -> {
                CacheEntity cacheEntity = cacheKeyEntity.getCacheEntity();
                CacheMethodDto cacheMethodDto = getCacheMethod(cacheEntity, appName);
                classCache.put(cacheEntity.getClassName(), cacheMethodDto);
            });
        } else {
            return getAllCache(appName);
        }
        return getClassCache(classCache.asMap());
    }

    /**
     * cacheName视图，className存放cacheName
     *
     * @param searchParam 查询条件
     * @return 查询结果
     */
    private Collection<ClassCacheDto> searchForCacheName(SearchParam searchParam, String appName) {
        Multimap<String, CacheMethodDto> classCache = ArrayListMultimap.create();
        Collection<CacheNameEntity> all = new ArrayList<>();
        if (!Strings.isNullOrEmpty(searchParam.getClassName())) {
            Iterable<CacheEntity> allByClassName = cacheRepository.findAllByClassNameLikeAndAppName(getLike(searchParam.getClassName()), appName);
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
            CacheMethodDto cacheMethodDto = getCacheMethod(cacheEntity, appName);
            classCache.put(cacheNameEntity.getCacheName(), cacheMethodDto);
        }
        return getClassCache(classCache.asMap());

    }

    private String getLike(String param) {
        return "%" + param + "%";
    }

    private Collection<ClassCacheDto> getClassCache(Map<String, Collection<CacheMethodDto>> map) {
        Collection<ClassCacheDto> classCacheDtos = new ArrayList<>();
        map.forEach((s, cacheMethodDtos) -> {
            ClassCacheDto classCacheDto = new ClassCacheDto(cacheMethodDtos);
            classCacheDto.setClassName(s);
            classCacheDtos.add(classCacheDto);
        });
        return classCacheDtos;
    }

    private CacheMethodDto getCacheMethod(CacheEntity cacheEntity, String appName) {
        CacheMethodDto cacheMethodDto = new CacheMethodDto();
        cacheMethodDto.setMethodName(cacheEntity.getMethodName());
        cacheMethodDto.setCacheManagerDtos(cacheManagerDtos(cacheEntity, appName));
        return cacheMethodDto;
    }


    @Override
    public Collection<ClassCacheDto> getAllCache(String appName) {
        Multimap<String, CacheMethodDto> classCache = ArrayListMultimap.create();
        for (CacheEntity cacheEntity : cacheRepository.findAllByAppName(appName)) {
            CacheMethodDto cacheMethodDto = new CacheMethodDto();
            cacheMethodDto.setMethodName(cacheEntity.getMethodName());
            cacheMethodDto.setCacheManagerDtos(cacheManagerDtos(cacheEntity, appName));
            classCache.put(cacheEntity.getClassName(), cacheMethodDto);
        }
        return getClassCache(classCache.asMap());
    }

    private Collection<CacheManagerDto> cacheManagerDtos(CacheEntity cacheEntity, String appName) {
        Collection<CacheManagerDto> cacheManagerDtos = new ArrayList<>();
        CacheManagerDto cacheManagerDto = new CacheManagerDto();
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
    public Collection<CacheKeyEntityDto> findCacheKeys(String cacheName, String appName) {
        Collection<CacheKeyEntityDto> allKey = new ArrayList<>();
        Collection<CacheNameEntity> byCacheName = cacheNameRepository.findByCacheNameAndAppName(cacheName, appName);
        byCacheName.forEach(cacheNameEntity -> {
            Collection<CacheKeyEntity> byCacheEntity_idAndAppName = cacheKeyRepository.findByCacheEntity_IdAndAppName(cacheNameEntity.getCacheEntity().getId(), appName);
            for (CacheKeyEntity cacheKeyEntity : byCacheEntity_idAndAppName) {
                CacheKeyEntityDto cacheKeyEntityDto = new CacheKeyEntityDto();
                BeanUtils.copyProperties(cacheKeyEntity, cacheKeyEntityDto);
                allKey.add(cacheKeyEntityDto);
            }
        });
        return allKey;
    }

    @Override
    public void removeCacheName(String cacheName, String key, String appName) {
        cacheNameRepository.findByCacheNameAndAppName(cacheName, appName).forEach(cacheNameEntity -> {
            if (Strings.isNullOrEmpty(key)) {
                for (CacheKeyEntity cacheKeyEntity : cacheKeyRepository.findByCacheEntity_IdAndAppName(cacheNameEntity.getCacheEntity().getId(), appName)) {
                    cacheKeyRepository.delete(cacheKeyEntity);
                }
            } else {
                cacheKeyRepository.removeByCacheEntity_IdAndCacheKeyAndAppName(cacheNameEntity.getCacheEntity().getId(), key, appName);
            }
        });

    }

    @Override
    public void removeClassName(String className, String key, String appName) {
        cacheRepository.findAllByClassNameAndAppName(className, appName).forEach(cacheEntity -> {
            Collection<CacheNameEntity> byCacheEntity_idAndAppName = cacheNameRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName);
            for (CacheNameEntity cacheNameEntity : byCacheEntity_idAndAppName) {
                if (Strings.isNullOrEmpty(key)) {
                    for (CacheKeyEntity cacheKeyEntity : cacheKeyRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName)) {
                        cacheKeyRepository.delete(cacheKeyEntity);
                        cacheManageClientService.cacheEvict(cacheNameEntity.getCacheName(), cacheKeyEntity.getCacheKey());
                    }
                } else {
                    cacheKeyRepository.removeByCacheEntity_IdAndCacheKeyAndAppName(cacheEntity.getId(), key, appName);
                    cacheManageClientService.cacheEvict(cacheNameEntity.getCacheName(), key);
                }
            }
        });
    }
}
