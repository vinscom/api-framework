package in.erail.service;

import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.apache.logging.log4j.Logger;
import in.erail.glue.annotation.StartService;

/**
 *
 * @author vinay
 */
public abstract class RESTServiceImpl implements RESTService {

  private boolean mBodyAsJson = true;
  private String mOperationId;
  private String mServiceUniqueId;
  private Vertx mVertx;
  private boolean mEnable = false;
  private Logger mLog;

  @StartService
  public void start() {
    if (mEnable) {
      getVertx()
              .eventBus()
              .<JsonObject>consumer(getServiceUniqueId())
              .toFlowable()
              .subscribeOn(Schedulers.io())
              .doOnSubscribe((s) -> getLog().info(() -> String.format("%s[%s] service started", getServiceUniqueId(), Thread.currentThread().getName())))
              .doOnTerminate(() -> getLog().info(() -> String.format("%s[%s] service stopped", getServiceUniqueId(), Thread.currentThread().getName())))
              .subscribe(this::process, err -> getLog().error(() -> String.format("Process exception:[%s],Error:[%s]", getServiceUniqueId(), err)));
    }
  }

  @Override
  public String getOperationId() {
    return mOperationId;
  }

  @Override
  public String getServiceUniqueId() {
    return mServiceUniqueId;
  }

  public void setOperationId(String pOperationId) {
    this.mOperationId = pOperationId;
  }

  public void setServiceUniqueId(String pServiceUniqueId) {
    this.mServiceUniqueId = pServiceUniqueId;
  }

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  public boolean isEnable() {
    return mEnable;
  }

  public void setEnable(boolean pEnable) {
    this.mEnable = pEnable;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public boolean isBodyAsJson() {
    return mBodyAsJson;
  }

  public void setBodyAsJson(boolean pBodyAsJson) {
    this.mBodyAsJson = pBodyAsJson;
  }

}
