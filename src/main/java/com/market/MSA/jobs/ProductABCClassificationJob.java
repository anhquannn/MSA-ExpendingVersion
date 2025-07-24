package com.market.MSA.jobs;

import com.market.MSA.services.product.ProductClassificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/** Quartz job to classify products into ABC categories. */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
public class ProductABCClassificationJob implements Job {

  private final ProductClassificationService productClassificationService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("Running ProductABCClassificationJob ...");
    productClassificationService.classifyProducts();
  }
}
