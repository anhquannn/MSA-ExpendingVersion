package com.market.MSA.jobs;

import com.market.MSA.repositories.order.CampaignRepository;
import java.time.LocalDateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateCampaignStatusJob implements Job {
  private final CampaignRepository campaignRepository;

  public UpdateCampaignStatusJob(CampaignRepository campaignRepository) {
    this.campaignRepository = campaignRepository;
  }

  @Override
  @Transactional
  public void execute(JobExecutionContext context) {
    LocalDateTime currentDate = LocalDateTime.now();
    campaignRepository.updateActiveCampaigns(currentDate);
    campaignRepository.updateExpiredCampaigns(currentDate);
  }
}
