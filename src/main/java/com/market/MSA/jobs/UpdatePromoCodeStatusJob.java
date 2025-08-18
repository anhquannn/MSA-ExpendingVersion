package com.market.MSA.jobs;

import com.market.MSA.repositories.order.PromoCodeRepository;
import java.time.LocalDateTime;
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
    LocalDateTime currentDate = LocalDateTime.now();
    promoCodeRepository.updateActivePromoCodes(currentDate);
    promoCodeRepository.updateExpiredPromoCodes(currentDate);
  }
}
