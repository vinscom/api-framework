package in.erail.scheduler;

import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Properties;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import in.erail.glue.annotation.StartService;

/**
 *
 * @author vinay
 */
public class QuartzScheduler {

  private Properties mConfig;
  private Scheduler mScheduler;
  private Logger mLog;
  private boolean mEnable;

  @StartService
  public void start() throws SchedulerException {
    mScheduler = new StdSchedulerFactory(getConfig()).getScheduler();
    if (mEnable) {
      mScheduler.start();
      getLog().info("Scheduler Started");
    }
  }

  public String addScheduledJob(QuartzScheduledJob pScheduledJob) {
    String uuid = UUID.randomUUID().toString();
    JobKey jobKey = new JobKey(uuid, pScheduledJob.getJobName());
    TriggerKey tirggerKey = new TriggerKey(uuid, pScheduledJob.getJobName());

    QuartzJob job = pScheduledJob.getJob();

    JobDetail jobDetails = JobBuilder
            .newJob(job.getClass())
            .withDescription(pScheduledJob.getJobDescription())
            .withIdentity(jobKey)
            .usingJobData(QuartzJobFactory.COMPONENT_PATH, job.getComponentPath())
            .build();

    Trigger trigger = newTrigger()
            .withIdentity(tirggerKey)
            .startNow()
            .withSchedule(pScheduledJob.getScheduleBuilder())
            .usingJobData(QuartzJobFactory.COMPONENT_PATH, job.getComponentPath())
            .build();

    try {
      mScheduler.scheduleJob(jobDetails, trigger);
      getLog().debug(() -> "Added Job:" + jobKey.toString());
    } catch (SchedulerException ex) {
      getLog().error(ex);
    }

    return jobKey.toString();
  }

  public Properties getConfig() {
    return mConfig;
  }

  public void setConfig(Properties pConfig) {
    this.mConfig = pConfig;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public boolean isEnable() {
    return mEnable;
  }

  public void setEnable(boolean pEnable) {
    this.mEnable = pEnable;
  }

  public Scheduler getScheduler() {
    return mScheduler;
  }

}
