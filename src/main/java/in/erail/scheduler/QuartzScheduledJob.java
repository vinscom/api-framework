package in.erail.scheduler;

import io.vertx.core.json.JsonObject;
import java.util.Optional;
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
  private JsonObject mAuxData;

  public QuartzScheduledJob(String pJobName, String pJobDescription, QuartzJob pJob, ScheduleBuilder<? extends Trigger> pScheduleBuilder) {
    this(pJobName, pJobDescription, pJob, pScheduleBuilder, null);
  }

  public QuartzScheduledJob(String pJobName, String pJobDescription, QuartzJob pJob, ScheduleBuilder<? extends Trigger> pScheduleBuilder, JsonObject pAuxData) {
    this.mJobName = pJobName;
    this.mJobDescription = pJobDescription;
    this.mJob = pJob;
    this.mScheduleBuilder = pScheduleBuilder;
    this.mAuxData = pAuxData;
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

  public JsonObject getAuxData() {
    return mAuxData;
  }

  public void setAuxData(JsonObject pAuxData) {
    this.mAuxData = pAuxData;
  }

}
