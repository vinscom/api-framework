package in.erail.scheduler;

import org.quartz.Job;

/**
 *
 * @author vinay
 */
public abstract class QuartzJob implements Job {

  private String mGlueMountPath;

  public String getGlueMountPath() {
    return mGlueMountPath;
  }

  public void setGlueMountPath(String pGlueMountPath) {
    this.mGlueMountPath = pGlueMountPath;
  }

}
