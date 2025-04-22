package com.market.MSA.jobs;

import com.market.MSA.models.user.InvalidatedToken;
import com.market.MSA.repositories.user.InvalidatedTokenRepository;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class UpdateExpiryTimeJob implements Job {
  private final InvalidatedTokenRepository invalidatedTokenRepository;

  public UpdateExpiryTimeJob(InvalidatedTokenRepository invalidatedTokenRepository) {
    this.invalidatedTokenRepository = invalidatedTokenRepository;
  }

  @Override
  @Transactional
  public void execute(JobExecutionContext context) {
    Date currentDate = new Date();
    List<InvalidatedToken> invalidatedTokens = invalidatedTokenRepository.findAll();
    for (InvalidatedToken invalidatedToken : invalidatedTokens) {
      long diffInMillies = currentDate.getTime() - invalidatedToken.getExpiryTime().getTime();
      long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

      if (diffInDays > 3) {
        invalidatedTokenRepository.delete(invalidatedToken);
      }
    }
  }
}
