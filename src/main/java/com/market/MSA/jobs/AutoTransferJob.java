package com.market.MSA.jobs;

import com.market.MSA.models.product.Transfer;
import com.market.MSA.services.others.NotificationService;
import com.market.MSA.services.product.AutoTransferService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Quartz job that checks low stock and triggers transfer requests automatically. */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class AutoTransferJob implements Job {

  private final AutoTransferService autoTransferService;
  private final NotificationService notificationService;

  @Override
  @Transactional
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("🔄 Executing AutoTransferJob...");

    try {
      List<Transfer> createdTransfers = autoTransferService.processLowStock();

      for (Transfer transfer : createdTransfers) {
        try {
          notificationService.sendAutoTransferCreatedNotification(transfer);
        } catch (Exception e) {
          log.warn(
              "Failed to send auto transfer notification for transfer #{}",
              transfer.getTransferRequestId(),
              e);
        }
      }

      log.info("✅ AutoTransferJob completed. Created {} transfers", createdTransfers.size());
    } catch (Exception e) {
      log.error("❌ Error in AutoTransferJob", e);
      throw new JobExecutionException(e);
    }
  }
}
