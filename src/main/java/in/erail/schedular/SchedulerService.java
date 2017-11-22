package in.erail.schedular;

import in.erail.glue.annotation.StartService;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public abstract class SchedulerService {

  private Logger mLog;
  private SchedulerType mSchedularType;
  private TimeUnit mTimeUnit;
  private long mInterval;
  private boolean mRecurring;

  @StartService
  public void start() {

    Scheduler scheduler;

    switch (mSchedularType) {
      case COMPUTATION:
        scheduler = Schedulers.computation();
        break;
      case IO:
        scheduler = Schedulers.io();
        break;
      case NEWTHREAD:
        scheduler = Schedulers.newThread();
        break;
      case SINGLE:
        scheduler = Schedulers.single();
        break;
      default:
        scheduler = Schedulers.io();
    }

    if (isRecurring()) {
      Observable
              .interval(getInterval(), getTimeUnit(), scheduler)
              .subscribe(this::performScheduledTask);
    } else {
      Observable
              .timer(getInterval(), getTimeUnit(), scheduler)
              .subscribe(this::performScheduledTask);
    }
  }

  public void performScheduledTask(Long pId) {
  }

  public SchedulerType getSchedularType() {
    return mSchedularType;
  }

  public void setSchedularType(SchedulerType pSchedularType) {
    this.mSchedularType = pSchedularType;
  }

  public TimeUnit getTimeUnit() {
    return mTimeUnit;
  }

  public void setTimeUnit(TimeUnit pTimeUnit) {
    this.mTimeUnit = pTimeUnit;
  }

  public long getInterval() {
    return mInterval;
  }

  public void setInterval(long pInterval) {
    this.mInterval = pInterval;
  }

  public boolean isRecurring() {
    return mRecurring;
  }

  public void setRecurring(boolean pRecurring) {
    this.mRecurring = pRecurring;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

}
