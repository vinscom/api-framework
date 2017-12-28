package in.erail.service;

import io.reactivex.Completable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vinay
 */
public class DummySingletonService extends SingletonServiceImpl {

  private List<String> mRecorder = new ArrayList();

  @Override
  public Completable startService() {
    mRecorder.add("start");
    return Completable.complete();
  }

  public List<String> getRecorder() {
    return mRecorder;
  }

  public void setRecorder(List<String> pRecorder) {
    this.mRecorder = pRecorder;
  }

}
