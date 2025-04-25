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
      Trigger lowStockCheckTrigger) {
    return args -> {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();

      // Đăng ký các job và trigger
      scheduler.scheduleJob(updateExpiryTimeJobDetail, updateExpiryTimeTrigger);
      scheduler.scheduleJob(updatePromoCodeStatusJobDetail, updatePromoCodeStatusTrigger);
      scheduler.scheduleJob(updateCampaignStatusJobDetail, updateCampaignStatusTrigger);
      scheduler.scheduleJob(createTrendingProductDataJobDetail, createTrendingProductDataTrigger);
      scheduler.scheduleJob(lowStockCheckJobDetail, lowStockCheckTrigger);

      // Khởi động scheduler (nếu chưa tự động chạy)
      if (!scheduler.isStarted()) {
        scheduler.start();
      }

      Thread.sleep(5000); // Chờ 5 giây để kiểm tra
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
}
