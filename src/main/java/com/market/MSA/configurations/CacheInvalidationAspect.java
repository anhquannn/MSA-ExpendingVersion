package com.market.MSA.configurations;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aspect that automatically clears all caches defined via {@link Cacheable} in the same service
 * class whenever a {@link Transactional} write-operation method succeeds.
 *
 * <p>This eliminates the need to add {@code @CacheEvict} annotations manually for every create /
 * update / delete method. Any method located in {@code com.market.MSA.services..*} package that: 1.
 * Is annotated with {@code @Transactional} 2. Is NOT annotated with {@code @Cacheable} will trigger
 * eviction of every cache name declared by {@code @Cacheable} annotations in the same class.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheInvalidationAspect {

  private final CacheManager cacheManager;
  private final ApplicationContext applicationContext;

  // Pointcut: any transactional method in service layer
  @Pointcut(
      "execution(* com.market.MSA.services..*(..)) && @annotation(org.springframework.transaction.annotation.Transactional)")
  public void transactionalServiceOperation() {}

  // After successful completion
  @AfterReturning("transactionalServiceOperation()")
  public void evictCaches(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    // Skip if method itself is a @Cacheable read method.
    if (method.isAnnotationPresent(Cacheable.class)) {
      return;
    }

    Object target = joinPoint.getTarget();
    Class<?> targetClass = target.getClass();

    Set<String> cacheNamesToEvict = new HashSet<>();

    // Caches declared in the same class
    for (Method m : targetClass.getDeclaredMethods()) {
      Cacheable cacheable = AnnotationUtils.findAnnotation(m, Cacheable.class);
      if (cacheable != null) {
        cacheNamesToEvict.addAll(Arrays.asList(cacheable.value()));
      }
    }

    // Caches declared in ANY service bean (cross-service invalidation)
    String basePackage = "com.market.MSA.services";
    for (String beanName : applicationContext.getBeanDefinitionNames()) {
      Object bean = applicationContext.getBean(beanName);
      Class<?> beanClass = AopUtils.getTargetClass(bean);
      Package pkg = beanClass.getPackage();
      if (pkg == null || !pkg.getName().startsWith(basePackage)) {
        continue;
      }
      for (Method m : beanClass.getDeclaredMethods()) {
        Cacheable cacheable = AnnotationUtils.findAnnotation(m, Cacheable.class);
        if (cacheable != null) {
          cacheNamesToEvict.addAll(Arrays.asList(cacheable.value()));
        }
      }
    }

    for (String cacheName : cacheNamesToEvict) {
      Cache cache = cacheManager.getCache(cacheName);
      if (cache != null) {
        log.debug(
            "Evicting cache '{}' due to data mutation in {}.{}",
            cacheName,
            targetClass.getSimpleName(),
            method.getName());
        cache.clear();
      }
    }
  }
}
