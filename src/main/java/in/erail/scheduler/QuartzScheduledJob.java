package in.erail.scheduler;

import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;

/**
 *
 * @author vinay
 */
public class QuartzScheduledJob {

  private String mJobName;
  private String mJobDescription;
  private QuartzJob mJob;
  private ScheduleBuilder<? extends Trigger> mScheduleBuilder;

  public QuartzScheduledJob(String pJobName, String pJobDescription, QuartzJob pJob, ScheduleBuilder<? extends Trigger> pScheduleBuilder) {
    this.mJobName = pJobName;
    this.mJobDescription = pJobDescription;
    this.mJob = pJob;
    this.mScheduleBuilder = pScheduleBuilder;
  }

  public String getJobName() {
    return mJobName;
  }

  public void setJobName(String pJobName) {
    this.mJobName = pJobName;
  }

  public String getJobDescription() {
    return mJobDescription;
  }

  public void setJobDescription(String pJobDescription) {
    this.mJobDescription = pJobDescription;
  }

  public QuartzJob getJob() {
    return mJob;
  }

  public void setJob(QuartzJob pJob) {
    this.mJob = pJob;
  }

  public ScheduleBuilder<? extends Trigger> getScheduleBuilder() {
    return mScheduleBuilder;
  }

  public void setScheduleBuilder(ScheduleBuilder<? extends Trigger> pScheduleBuilder) {
    this.mScheduleBuilder = pScheduleBuilder;
  }

}
