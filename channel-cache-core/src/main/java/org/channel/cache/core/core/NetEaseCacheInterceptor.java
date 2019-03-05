package org.channel.cache.core.core;

import org.channel.cache.core.config.CacheWebProperties;
import org.channel.cache.core.core.duplicate.CacheAspectSupport;
import org.channel.cache.core.dto.CacheDto;
import org.channel.cache.core.dto.CacheNameDto;
import org.channel.cache.core.dto.CacheProjectDto;
import org.channel.cache.core.storage.ProjectCacheInvoke;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author zhangchanglu
 * @since 2018/09/26 14:52.
 */
@Slf4j
public class NetEaseCacheInterceptor extends CacheAspectSupport implements MethodInterceptor, Serializable, ApplicationListener<ContextRefreshedEvent> {
    private MultiValueMap<CacheOperation, NetEaseCacheOperationContext> contexts = null;

    @Override
    @Nullable
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();

        CacheOperationInvoker aopAllianceInvoker = () -> {
            try {
                return invocation.proceed();
            } catch (Throwable ex) {
                throw new CacheOperationInvoker.ThrowableWrapper(ex);
            }
        };

        try {
            return execute(aopAllianceInvoker, invocation.getThis(), method, invocation.getArguments());
        } catch (CacheOperationInvoker.ThrowableWrapper th) {
            throw th.getOriginal();
        }
    }

    public class NetEaseCacheOperationContext extends CacheOperationContext {
        @Override
        public Collection<? extends Cache> getCaches() {
            return super.getCaches();
        }

        public Object getKey() {
            return generateKey(new Object());
        }

        public NetEaseCacheOperationContext(CacheOperationMetadata metadata, Object[] args, Object target) {
            super(metadata, args, target);
        }
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        List<CacheDto> cacheConfig = new ArrayList<>();
        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, ProjectCacheInvoke> cacheStorageMap = applicationContext.getBeansOfType(ProjectCacheInvoke.class);
        if (cacheStorageMap.isEmpty()) {
            log.warn("can't load ProjectCacheInvoke");
            return;
        }
        ProjectCacheInvoke projectCacheInvoke = cacheStorageMap.entrySet().iterator().next().getValue();
        Map<String, NetEaseCacheHandler> beansOfType = applicationContext.getBeansOfType(NetEaseCacheHandler.class);
        beansOfType.forEach((s, netEaseCacheHandler) -> {
            netEaseCacheHandler.setProjectCacheInvoke(projectCacheInvoke);
            addHandler(netEaseCacheHandler);
        });
        Map<String, CacheWebProperties> cacheWebPropertiesMap = applicationContext.getBeansOfType(CacheWebProperties.class);
        if (CollectionUtils.isEmpty(cacheStorageMap)) {
            log.warn("load bean CacheWebProperties fail");
            return;
        }
        String appName = cacheWebPropertiesMap.entrySet().iterator().next().getValue().getAppName();
        AnnotationCacheOperationSource annotationCacheOperationSource = new AnnotationCacheOperationSource();
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String s : beanDefinitionNames) {
            final Object bean;
            try {
                bean = applicationContext.getBean(s);
            } catch (BeansException e) {
                log.warn("load bean - " + s);
                continue;
            }
            ReflectionUtils.doWithMethods(bean.getClass(), method -> {
                if (!method.toString().contains("$$")) {
                    Collection<CacheOperation> cacheOperations = annotationCacheOperationSource.getCacheOperations(method, bean.getClass());
                    if (!CollectionUtils.isEmpty(cacheOperations)) {
                        for (CacheOperation cacheOperation : cacheOperations) {
                            CacheDto cacheDto=new CacheDto();
                            cacheDto.setAppName(appName);
                            cacheDto.setClassName(getTargetClassName(bean));
                            cacheDto.setMethodName(method.getName());
                            cacheDto.setCacheConfigKey(cacheOperation.getKey());
                            cacheDto.setCacheOperation(cacheOperation.getClass().getSimpleName());
                            Collection<CacheNameDto> cacheNameDtos=new ArrayList<>();
                            //每个cache信息的详情
                            for (String cacheName : cacheOperation.getCacheNames()) {
                                CacheNameDto cacheNameDto = new CacheNameDto();
                                cacheNameDto.setCacheName(cacheName);
                                cacheNameDto.setAppName(appName);
                                cacheNameDtos.add(cacheNameDto);
                            }
                            cacheDto.setCacheNameDtos(cacheNameDtos);
                            cacheConfig.add(cacheDto);
                        }
                    }
                }
            });
        }
        CacheProjectDto cacheProjectDto = new CacheProjectDto();
        cacheProjectDto.setAppName(appName);
        cacheProjectDto.setCacheDtos(cacheConfig);
        projectCacheInvoke.allCacheConfig(cacheProjectDto);
    }
    private String getTargetClassName(Object bean){
        String name = bean.getClass().getName();
        return name.substring(0,name.indexOf("$$"));
    }
}
