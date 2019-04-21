package in.erail.scheduler;

import org.quartz.Job;

/**
 *
 * @author vinay
 */
public abstract class QuartzJob implements Job {

  private String mMountPath;

  public String getMountPath() {
    return mMountPath;
  }

  public void setMountPath(String pMountPath) {
    this.mMountPath = pMountPath;
  }

}
