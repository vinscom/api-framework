package in.erail.scheduler;

import in.erail.glue.annotation.StartService;
import io.vertx.junit5.VertxTestContext;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.ScheduleBuilder;
import org.quartz.SchedulerException;

/**
 *
 * @author vinay
 */
public class HelloScheduler extends QuartzJob {

  private String mMessage;
  private QuartzScheduler mScheduler;
  private Logger mLog;
  private VertxTestContext mVertxTestContext;
  private String mCronExp;

  @StartService
  public void start() throws SchedulerException {
    ScheduleBuilder<?> sb = CronScheduleBuilder.cronSchedule(getCronExp());
    QuartzScheduledJob sj = new QuartzScheduledJob("Test", "Nothing", this, sb);
    getScheduler().addScheduledJob(sj);
  }

  @Override
  public void execute(JobExecutionContext pArg0) throws JobExecutionException {
    if (mVertxTestContext != null) {
      Optional data = Optional.ofNullable(pArg0.getJobDetail().getJobDataMap().get(QuartzJob.JOB_DATA_AUX));
      if (data.isPresent()) {
        mVertxTestContext.completeNow();
      } else {
        mVertxTestContext.failNow(new RuntimeException("Aux data not found"));
      }
    }
  }

  public QuartzScheduler getScheduler() {
    return mScheduler;
  }

  public void setScheduler(QuartzScheduler pScheduler) {
    this.mScheduler = pScheduler;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public String getMessage() {
    return mMessage;
  }

  public void setMessage(String pMessage) {
    this.mMessage = pMessage;
  }

  public VertxTestContext getVertxTestContext() {
    return mVertxTestContext;
  }

  public void setVertxTestContext(VertxTestContext pVertxTestContext) {
    this.mVertxTestContext = pVertxTestContext;
  }

  public String getCronExp() {
    return mCronExp;
  }

  public void setCronExp(String pCronExp) {
    this.mCronExp = pCronExp;
  }

}
