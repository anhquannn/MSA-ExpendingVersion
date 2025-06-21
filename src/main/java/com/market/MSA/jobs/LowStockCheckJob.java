package com.market.MSA.jobs;

import com.market.MSA.services.others.NotificationService;
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
    try {
      notificationService.checkAndNotifyLowStock();
    } catch (Exception e) {
      throw new JobExecutionException(e);
    }
  }
}
