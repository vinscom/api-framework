package in.erail.service;

import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.apache.logging.log4j.Logger;
import in.erail.glue.annotation.StartService;
import in.erail.model.RequestEvent;
import in.erail.model.ResponseEvent;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.vertx.reactivex.core.eventbus.Message;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author vinay
 */
public abstract class RESTServiceImpl implements RESTService {

  private String mOperationId;
  private String mServiceUniqueId;
  private Vertx mVertx;
  private boolean mEnable = false;
  private Logger mLog;
  private Scheduler mScheduler = Schedulers.io();

  @StartService
  public void start() {
    if (mEnable) {
      getVertx()
              .eventBus()
              .<JsonObject>consumer(getServiceUniqueId())
              .toFlowable()
              .subscribeOn(getScheduler())
              .doOnSubscribe((s) -> getLog().info(() -> String.format("%s[%s] service started", getServiceUniqueId(), Thread.currentThread().getName())))
              .doOnTerminate(() -> getLog().info(() -> String.format("%s[%s] service stopped", getServiceUniqueId(), Thread.currentThread().getName())))
              .flatMapSingle(this::handleRequest)
              .subscribe(
                      resp -> getLog().trace(() -> resp.toString()), 
                      err -> getLog().error(() -> String.format("Process exception:[%s],Error:[%s]", getServiceUniqueId(), ExceptionUtils.getStackTrace(err)))
              );
    }
  }

  public Single<JsonObject> handleRequest(Message<JsonObject> pMessage) {
    return Single
            .just(pMessage)
            .map(m -> pMessage.body().mapTo(RequestEvent.class))
            .flatMapMaybe(req -> process(req))
            .toSingle(new ResponseEvent())
            .map(resp -> JsonObject.mapFrom(resp))
            .doOnSuccess(resp -> pMessage.reply(resp));
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

  public Scheduler getScheduler() {
    return mScheduler;
  }

  public void setScheduler(Scheduler pScheduler) {
    this.mScheduler = pScheduler;
  }

}
