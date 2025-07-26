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

/**
 * Aspect tự động xóa các cache được định nghĩa bởi @Cacheable trong cùng service class hoặc các
 * service khác khi một phương thức @Transactional (không phải @Cacheable) hoàn thành.
 *
 * <p>Mục đích: Loại bỏ nhu cầu thêm @CacheEvict thủ công cho các phương thức thay đổi dữ liệu.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheInvalidationAspect {

  // Tiêm CacheManager để quản lý cache và ApplicationContext để truy cập các bean
  private final CacheManager cacheManager;
  private final ApplicationContext applicationContext;

  /**
   * Pointcut: Xác định các phương thức trong package com.market.MSA.services có
   * annotation @Transactional.
   */
  @Pointcut(
      "(execution(* com.market.MSA.services..*(..)) || execution(* com.market.MSA.jobs..*(..))) && @annotation(org.springframework.transaction.annotation.Transactional)")
  public void transactionalServiceOperation() {}

  /**
   * Advice: Được gọi sau khi phương thức @Transactional hoàn thành thành công. Xóa tất cả cache
   * được định nghĩa bởi @Cacheable trong cùng class hoặc các class khác trong package
   * com.market.MSA.services.
   */
  @AfterReturning("transactionalServiceOperation()")
  public void evictCaches(JoinPoint joinPoint) {
    // Lấy thông tin phương thức được gọi
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    // Nếu phương thức có @Cacheable (phương thức đọc), bỏ qua để tránh xóa cache của chính nó
    if (method.isAnnotationPresent(Cacheable.class)) {
      return;
    }

    // Lấy class chứa phương thức
    Object target = joinPoint.getTarget();
    Class<?> targetClass = target.getClass();

    // Danh sách các cache name cần xóa
    Set<String> cacheNamesToEvict = new HashSet<>();

    // 1. Thu thập cache names từ các phương thức @Cacheable trong cùng class
    for (Method m : targetClass.getDeclaredMethods()) {
      Cacheable cacheable = AnnotationUtils.findAnnotation(m, Cacheable.class);
      if (cacheable != null) {
        cacheNamesToEvict.addAll(Arrays.asList(cacheable.value()));
      }
    }

    // 2. Thu thập cache names từ tất cả các bean trong package com.market.MSA.services
    String basePackage = "com.market.MSA.services";
    for (String beanName : applicationContext.getBeanDefinitionNames()) {
      Object bean = applicationContext.getBean(beanName);
      Class<?> beanClass = AopUtils.getTargetClass(bean);
      Package pkg = beanClass.getPackage();
      // Kiểm tra xem bean có thuộc package com.market.MSA.services không
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

    // 3. Xóa tất cả các cache trong danh sách
    for (String cacheName : cacheNamesToEvict) {
      Cache cache = cacheManager.getCache(cacheName);
      if (cache != null) {
        log.debug(
            "Evicting cache '{}' due to data mutation in {}.{}",
            cacheName,
            targetClass.getSimpleName(),
            method.getName());
        cache.clear(); // Xóa cache
      }
    }
  }
}
