package com.market.MSA.configurations;

import com.market.MSA.jobs.*;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
@Slf4j
public class QuartzConfig {
  private final DataSource dataSource;
  private final AutowireCapableBeanFactory beanFactory;

  public QuartzConfig(DataSource dataSource, AutowireCapableBeanFactory beanFactory) {
    this.dataSource = dataSource;
    this.beanFactory = beanFactory;
  }

  //  | Mô tả                        | Cron Expression        |
  //          | ---------------------------- | ---------------------- |
  //          | **Mỗi 5 phút**               | `"0 0/5 * * * ?"`      |
  //          | **Mỗi 10 phút**              | `"0 0/10 * * * ?"`     |
  //          | **Mỗi giờ**                  | `"0 0 * * * ?"`        |
  //          | **Mỗi ngày lúc 01:00 sáng**  | `"0 0 1 * * ?"`        |
  //          | **Mỗi thứ 2 lúc 09:00 sáng** | `"0 0 9 ? * MON"`      |
  //          | **Mỗi ngày làm việc 08:30**  | `"0 30 8 ? * MON-FRI"` |
  //          | **Mỗi 15 giây (demo)**       | `"0/15 * * * * ?"`     |

  @Bean
  public JobFactory jobFactory() {
    return new SpringBeanJobFactory() {
      @Override
      protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
      }
    };
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() {
    SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
    scheduler.setDataSource(dataSource);
    scheduler.setJobFactory(jobFactory());
    scheduler.setOverwriteExistingJobs(true);
    scheduler.setStartupDelay(3);
    scheduler.setAutoStartup(true);
    return scheduler;
  }

  @Bean
  public ApplicationRunner checkScheduler(
      SchedulerFactoryBean schedulerFactoryBean,
      JobDetail updateExpiryTimeJobDetail,
      Trigger updateExpiryTimeTrigger,
      JobDetail updatePromoCodeStatusJobDetail,
      Trigger updatePromoCodeStatusTrigger,
      JobDetail updateCampaignStatusJobDetail,
      Trigger updateCampaignStatusTrigger,
      JobDetail createTrendingProductDataJobDetail,
      Trigger createTrendingProductDataTrigger,
      JobDetail lowStockCheckJobDetail,
      Trigger lowStockCheckTrigger,
      JobDetail paymentTimeoutJobDetail,
      Trigger paymentTimeoutTrigger,
      JobDetail productDiscountJobDetail,
      Trigger productDiscountTrigger) { // Xóa 2 dòng autoTransferJob
    return args -> {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();

      scheduler.scheduleJob(updateExpiryTimeJobDetail, updateExpiryTimeTrigger);
      scheduler.scheduleJob(updatePromoCodeStatusJobDetail, updatePromoCodeStatusTrigger);
      scheduler.scheduleJob(updateCampaignStatusJobDetail, updateCampaignStatusTrigger);
      scheduler.scheduleJob(createTrendingProductDataJobDetail, createTrendingProductDataTrigger);
      scheduler.scheduleJob(lowStockCheckJobDetail, lowStockCheckTrigger);
      scheduler.scheduleJob(paymentTimeoutJobDetail, paymentTimeoutTrigger);
      scheduler.scheduleJob(productDiscountJobDetail, productDiscountTrigger);

      if (!scheduler.isStarted()) {
        scheduler.start();
      }
      Thread.sleep(5000);
    };
  }

  @Bean
  public JobDetail updateExpiryTimeJobDetail() {
    return JobBuilder.newJob(UpdateExpiryTimeJob.class)
        .withIdentity("updateExpiryTimeJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger updateExpiryTimeTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(updateExpiryTimeJobDetail())
        .withIdentity("updateExpiryTimeTrigger")
        .withSchedule(
            CronScheduleBuilder.cronSchedule("0 0 0 * * ?")
                .withMisfireHandlingInstructionFireAndProceed())
        .build();
  }

  @Bean
  public JobDetail updatePromoCodeStatusJobDetail() {
    return JobBuilder.newJob(UpdatePromoCodeStatusJob.class)
        .withIdentity("updatePromoCodeStatusJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger updatePromoCodeStatusTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(updatePromoCodeStatusJobDetail())
        .withIdentity("updatePromoCodeStatusTrigger")
        .withSchedule(
            CronScheduleBuilder.cronSchedule("0 0 0 * * ?")
                .withMisfireHandlingInstructionFireAndProceed())
        .build();
  }

  @Bean
  public JobDetail updateCampaignStatusJobDetail() {
    return JobBuilder.newJob(UpdateCampaignStatusJob.class)
        .withIdentity("updateCampaignStatusJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger updateCampaignStatusTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(updateCampaignStatusJobDetail())
        .withIdentity("updateCampaignStatusTrigger")
        .withSchedule(
            CronScheduleBuilder.cronSchedule("0 0 0 * * ?")
                .withMisfireHandlingInstructionFireAndProceed())
        .build();
  }

  @Bean
  public JobDetail createTrendingProductDataJobDetail() {
    return JobBuilder.newJob(TrendingProductJob.class)
        .withIdentity("createTrendingProductDataJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger createTrendingProductDataTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(createTrendingProductDataJobDetail())
        .withIdentity("createTrendingProductDataTrigger")
        .withSchedule(
            CronScheduleBuilder.cronSchedule("0 0 0 * * ?")
                .withMisfireHandlingInstructionFireAndProceed())
        .build();
  }

  @Bean
  public JobDetail lowStockCheckJobDetail() {
    return JobBuilder.newJob(LowStockCheckJob.class)
        .withIdentity("lowStockCheckJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger lowStockCheckTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(lowStockCheckJobDetail())
        .withIdentity("lowStockCheckTrigger")
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(60).repeatForever())
        .build();
  }

  @Bean
  public JobDetail paymentTimeoutJobDetail() {
    return JobBuilder.newJob(PaymentTimeoutScheduler.class)
        .withIdentity("paymentTimeoutJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger paymentTimeoutTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(paymentTimeoutJobDetail())
        .withIdentity("paymentTimeoutTrigger")
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(5).repeatForever())
        .build();
  }

  @Bean
  public JobDetail productDiscountJobDetail() {
    return JobBuilder.newJob(ProductDiscountScheduler.class)
        .withIdentity("productDiscountJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger productDiscountTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(productDiscountJobDetail())
        .withIdentity("productDiscountTrigger")
        .withSchedule(
            CronScheduleBuilder.cronSchedule("0 0 0 * * ?")
                .withMisfireHandlingInstructionFireAndProceed())
        .build();
  }

  // ================= Auto Transfer Stock Job =================
  @Bean
  public JobDetail autoTransferJobDetail() {
    return JobBuilder.newJob(AutoTransferJob.class)
        .withIdentity("autoTransferJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger autoTransferTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(autoTransferJobDetail())
        .withIdentity("autoTransferTrigger")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1).repeatForever())
        .build();
  }

  // ================= Auto Bundle Promotion Job =================
  @Bean
  public JobDetail autoBundlePromotionJobDetail() {
    return JobBuilder.newJob(com.market.MSA.jobs.AutoBundlePromotionJob.class)
        .withIdentity("autoBundlePromotionJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger autoBundlePromotionTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(autoBundlePromotionJobDetail())
        .withIdentity("autoBundlePromotionTrigger")
        // Chạy hàng ngày lúc 01:00
        .withSchedule(
            CronScheduleBuilder.cronSchedule("0 0 1 * * ?")
                .withMisfireHandlingInstructionFireAndProceed())
        .build();
  }

  // ================= ABC Classification Job =================
  @Bean
  public JobDetail productABCClassificationJobDetail() {
    return JobBuilder.newJob(ProductABCClassificationJob.class)
        .withIdentity("productABCClassificationJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger productABCClassificationTrigger() {
    return TriggerBuilder.newTrigger()
        .forJob(productABCClassificationJobDetail())
        .withIdentity("productABCClassificationTrigger")
        .withSchedule(
            CronScheduleBuilder.cronSchedule("0 0 0 * * ?")
                .withMisfireHandlingInstructionFireAndProceed())
        .build();
  }

  // ================= Extra Jobs Scheduler =================
  @Bean
  public ApplicationRunner extraJobsScheduler(
      SchedulerFactoryBean schedulerFactoryBean,
      JobDetail autoTransferJobDetail,
      Trigger autoTransferTrigger,
      JobDetail productABCClassificationJobDetail,
      Trigger productABCClassificationTrigger,
      JobDetail autoBundlePromotionJobDetail,
      Trigger autoBundlePromotionTrigger) {
    return args -> {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      scheduler.scheduleJob(autoTransferJobDetail, autoTransferTrigger);
      scheduler.scheduleJob(productABCClassificationJobDetail, productABCClassificationTrigger);
      scheduler.scheduleJob(autoBundlePromotionJobDetail, autoBundlePromotionTrigger);
      if (!scheduler.isStarted()) {
        scheduler.start();
      }
    };
  }
}
