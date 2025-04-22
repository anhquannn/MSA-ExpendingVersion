package com.market.MSA.jobs;

import com.market.MSA.repositories.order.PromoCodeRepository;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdatePromoCodeStatusJob implements Job {
  private final PromoCodeRepository promoCodeRepository;

  public UpdatePromoCodeStatusJob(PromoCodeRepository promoCodeRepository) {
    this.promoCodeRepository = promoCodeRepository;
  }

  @Override
  @Transactional
  public void execute(JobExecutionContext context) {
    Date currentDate = new Date();
    promoCodeRepository.updateActivePromoCodes(currentDate);
    promoCodeRepository.updateExpiredPromoCodes(currentDate);
  }
}
