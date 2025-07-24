package com.market.MSA.jobs;

import com.market.MSA.services.product.AutoTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/** Quartz job that checks low stock and triggers transfer requests automatically. */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
public class AutoTransferJob implements Job {

  private final AutoTransferService autoTransferService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("Executing AutoTransferJob ...");
    autoTransferService.processLowStock();
  }
}
