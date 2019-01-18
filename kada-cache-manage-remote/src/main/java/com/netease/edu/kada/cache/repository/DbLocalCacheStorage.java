package com.netease.edu.kada.cache.repository;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.netease.edu.kada.cache.client.CacheManageClientService;
import com.netease.edu.kada.cache.core.dto.PageInfo;
import com.netease.edu.kada.cache.core.storage.CacheStorage;
import com.netease.edu.kada.cache.core.storage.SearchParam;
import com.netease.edu.kada.cache.core.utils.PageToPageInfoUtils;
import com.netease.edu.kada.cache.core.vo.CacheKeyVo;
import com.netease.edu.kada.cache.core.vo.CacheManagerVo;
import com.netease.edu.kada.cache.core.vo.CacheMethodVo;
import com.netease.edu.kada.cache.core.vo.ClassCacheVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            return PageToPageInfoUtils.convertTOPageInfo(getClassCache(classCache.asMap()), allByClassNameLikeAndAppName);
        } else if (!Strings.isNullOrEmpty(searchParam.getCacheName())) {
            Iterable<CacheNameEntity> byCacheNameLike = cacheNameRepository.findByCacheNameLikeAndAppName(getLike(searchParam.getCacheName()), appName);
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
        return PageInfo.create(getClassCache(classCache.asMap()));
    }

    /**
     * cacheName视图，className存放cacheName
     *
     * @param searchParam 查询条件
     * @return 查询结果
     */
    private PageInfo<ClassCacheVo> searchForCacheName(SearchParam searchParam, String appName, Pageable pageable) {
        Collection<CacheNameEntity> all = new ArrayList<>();
        if (!Strings.isNullOrEmpty(searchParam.getClassName())) {
            Page<CacheEntity> allByClassName = cacheRepository.findAllByClassNameLikeAndAppName(getLike(searchParam.getClassName()), appName, pageable);
            for (CacheEntity cacheEntity : allByClassName) {
                all.add(cacheNameRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName));
            }
            return PageToPageInfoUtils.convertTOPageInfo(getClassCache(all, appName), allByClassName);
        } else if (!Strings.isNullOrEmpty(searchParam.getCacheName())) {
            for (CacheNameEntity cacheNameEntity : cacheNameRepository.findByCacheNameLikeAndAppName(getLike(searchParam.getCacheName()), appName)) {
                all.add(cacheNameEntity);
            }
        } else if (!Strings.isNullOrEmpty(searchParam.getCacheKey())) {
            Iterable<CacheKeyEntity> byKeyLike = cacheKeyRepository.findByCacheKeyLikeAndAppName(getLike(searchParam.getCacheKey()), appName);
            for (CacheKeyEntity cacheKeyEntity : byKeyLike) {
                CacheEntity cacheEntity = cacheKeyEntity.getCacheEntity();
                all.add(cacheNameRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName));
            }
        } else {
            Page<CacheNameEntity> cacheNameEntityPage = cacheNameRepository.findAll(pageable);
            for (CacheNameEntity cacheNameEntity : cacheNameEntityPage) {
                all.add(cacheNameEntity);
            }
            return PageToPageInfoUtils.convertTOPageInfo(getClassCache(all, appName), cacheNameEntityPage);
        }
        return PageInfo.create(getClassCache(all, appName));
    }

    private Collection<ClassCacheVo> getClassCache(Collection<CacheNameEntity> all, String appName) {
        Multimap<String, CacheMethodVo> classCache = ArrayListMultimap.create();
        for (CacheNameEntity cacheNameEntity : all) {
            CacheEntity cacheEntity = cacheNameEntity.getCacheEntity();
            CacheMethodVo cacheMethodDto = getCacheMethod(cacheEntity, appName);
            classCache.put(cacheNameEntity.getCacheName(), cacheMethodDto);
        }
        return getClassCache(classCache.asMap());
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
    public PageInfo<ClassCacheVo> getAllCache(String appName, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Multimap<String, CacheMethodVo> classCache = ArrayListMultimap.create();
        Page<CacheEntity> allByAppName = cacheRepository.findAllByAppName(appName, pageable);
        for (CacheEntity cacheEntity :allByAppName) {
            CacheMethodVo cacheMethodDto = new CacheMethodVo();
            cacheMethodDto.setMethodName(cacheEntity.getMethodName());
            cacheMethodDto.setCacheManagerDtos(cacheManagerDtos(cacheEntity, appName));
            classCache.put(cacheEntity.getClassName(), cacheMethodDto);
        }
        return PageToPageInfoUtils.convertTOPageInfo(getClassCache(classCache.asMap()),allByAppName);
    }

    @Override
    public Collection<String> getAllProject() {
        return cacheRepository.findAllGroupByAppName();
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
        CacheNameEntity cacheNameEntities = cacheNameRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName);
        if (Objects.isNull(cacheNameEntities)) {
            return cacheManagerDtos;
        }
        cacheManagerDto.setCacheNames(cacheNameEntities.getCacheName());
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
        try {
            cacheNameRepository.findByCacheNameAndAppName(cacheName, appName).forEach(cacheNameEntity -> {
                if (Strings.isNullOrEmpty(key)) {
                    for (CacheKeyEntity cacheKeyEntity : cacheKeyRepository.findByCacheEntity_IdAndAppName(cacheNameEntity.getCacheEntity().getId(), appName)) {
                        cacheManageClientService.cacheEvict(appName, cacheName, key);
                        cacheKeyRepository.delete(cacheKeyEntity);
                    }
                } else {
                    cacheManageClientService.cacheEvict(appName, cacheName, key);
                    cacheKeyRepository.removeByCacheEntity_IdAndCacheKeyAndAppName(cacheNameEntity.getCacheEntity().getId(), key, appName);
                }
            });
        } catch (Exception e) {
            log.warn("操作远程缓存失败cacheName:{},key:{},appName:{}", cacheName, key, appName, e);
            return false;
        }
        return true;
    }

    @Override
    public boolean removeClassName(String className, String key, String appName) {
        try {
            cacheRepository.findAllByClassNameAndAppName(className, appName).forEach(cacheEntity -> {
                CacheNameEntity cacheNameEntity = cacheNameRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName);
                if (Strings.isNullOrEmpty(key)) {
                    for (CacheKeyEntity cacheKeyEntity : cacheKeyRepository.findByCacheEntity_IdAndAppName(cacheEntity.getId(), appName)) {
                        cacheManageClientService.cacheEvict(appName, cacheNameEntity.getCacheName(), cacheKeyEntity.getCacheKey());
                        cacheKeyRepository.delete(cacheKeyEntity);
                    }
                } else {
                    cacheManageClientService.cacheEvict(appName, cacheNameEntity.getCacheName(), key);
                    cacheKeyRepository.removeByCacheEntity_IdAndCacheKeyAndAppName(cacheEntity.getId(), key, appName);
                }
            });
        } catch (Exception e) {
            log.warn("操作远程缓存失败className:{},key:{},appName:{}", className, key, appName, e);
            return false;
        }
        return true;
    }
}
