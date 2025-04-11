package com.market.MSA.jobs;

import com.market.MSA.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LowStockCheckJob implements Job {

  private final NotificationService notificationService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("Running low stock check job...");
    try {
      notificationService.checkAndNotifyLowStock();
      log.info("Low stock check completed successfully");
    } catch (Exception e) {
      log.error("Error in low stock check job", e);
      throw new JobExecutionException(e);
    }
  }
}
