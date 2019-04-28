package in.erail.scheduler;

import org.quartz.Job;

/**
 *
 * @author vinay
 */
public abstract class QuartzJob implements Job {

  public static final String JOB_DATA_AUX = "_aux";

  private String mGlueMountPath;

  public String getGlueMountPath() {
    return mGlueMountPath;
  }

  public void setGlueMountPath(String pGlueMountPath) {
    this.mGlueMountPath = pGlueMountPath;
  }

}
