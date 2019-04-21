package in.erail.scheduler;

import org.quartz.Job;

/**
 *
 * @author vinay
 */
public abstract class QuartzJob implements Job {

  private String mComponentPath;

  public String getComponentPath() {
    return mComponentPath;
  }

  public void setComponentPath(String pComponentPath) {
    this.mComponentPath = pComponentPath;
  }

}
