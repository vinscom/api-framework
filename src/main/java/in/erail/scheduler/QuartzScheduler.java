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
import io.vertx.core.json.JsonObject;
import java.util.Optional;

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

  public JobKey addScheduledJob(QuartzScheduledJob pScheduledJob) {
    String uuid = UUID.randomUUID().toString();
    JobKey jobKey = JobKey.jobKey(uuid, pScheduledJob.getJobName());
    TriggerKey tirggerKey = TriggerKey.triggerKey(uuid, pScheduledJob.getJobName());

    String auxData = Optional
            .ofNullable(pScheduledJob.getAuxData())
            .orElse(new JsonObject())
            .toString();

    QuartzJob job = pScheduledJob.getJob();

    JobDetail jobDetails = JobBuilder
            .newJob(job.getClass())
            .withDescription(pScheduledJob.getJobDescription())
            .withIdentity(jobKey)
            .usingJobData(QuartzJobFactory.COMPONENT_PATH, job.getGlueMountPath())
            .usingJobData(QuartzJob.JOB_DATA_AUX, auxData)
            .build();

    Trigger trigger = newTrigger()
            .withIdentity(tirggerKey)
            .startNow()
            .withSchedule(pScheduledJob.getScheduleBuilder())
            .build();

    try {
      mScheduler.scheduleJob(jobDetails, trigger);
      getLog().debug(() -> "Added Job:" + jobKey.toString());
    } catch (SchedulerException ex) {
      getLog().error(ex);
    }

    return jobKey;
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
