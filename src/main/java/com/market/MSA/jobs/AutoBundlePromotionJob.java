package com.market.MSA.jobs;

import com.market.MSA.services.product.AutoBundlePromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Quartz job dùng để tự động tạo khuyến mãi bundle A->C hằng ngày. Thay thế cho
 * annotation @Scheduled trong AutoBundlePromotionService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoBundlePromotionJob implements Job {

  private final AutoBundlePromotionService autoBundlePromotionService;

  @Override
  @Transactional
  public void execute(JobExecutionContext context) {
    log.info("[Quartz] AutoBundlePromotionJob is running ...");
    autoBundlePromotionService.generateInactivePromotions();
  }
}
