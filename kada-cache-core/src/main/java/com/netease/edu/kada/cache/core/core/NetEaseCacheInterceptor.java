package com.netease.edu.kada.cache.core.core;

import com.netease.edu.kada.cache.core.config.CacheWebProperties;
import com.netease.edu.kada.cache.core.core.duplicate.CacheAspectSupport;
import com.netease.edu.kada.cache.core.dto.CacheMethodDto;
import com.netease.edu.kada.cache.core.dto.ClassCacheDto;
import com.netease.edu.kada.cache.core.storage.ProjectCacheInvoke;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
        List<ClassCacheDto> cacheConfig = new ArrayList<>();
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
        AnnotationCacheOperationSource annotationCacheOperationSource = new AnnotationCacheOperationSource();
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String s : beanDefinitionNames) {
            Collection<CacheMethodDto> cacheMethodDtos = new ArrayList<>();
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
                            CacheMethodDto cacheMethodDto = new CacheMethodDto();
                            cacheMethodDto.setMethodName(method.getName());
                            cacheMethodDto.setCacheOperation(cacheOperation);
                            cacheMethodDtos.add(cacheMethodDto);
                        }
                    }
                }
            });
            if (!cacheMethodDtos.isEmpty()) {
                cacheConfig.add(new ClassCacheDto(bean.getClass(), cacheMethodDtos));
            }
        }
        Map<String, CacheWebProperties> cacheWebPropertiesMap = applicationContext.getBeansOfType(CacheWebProperties.class);
        if (CollectionUtils.isEmpty(cacheStorageMap)) {
            log.warn("load bean CacheWebProperties fail");
            return;
        }
        projectCacheInvoke.allCacheConfig(cacheConfig, cacheWebPropertiesMap.entrySet().iterator().next().getValue().getAppName());
    }
}
