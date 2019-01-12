package in.erail.service;

import com.google.common.net.MediaType;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.apache.logging.log4j.Logger;
import in.erail.glue.annotation.StartService;
import in.erail.model.RequestEvent;
import in.erail.model.ResponseEvent;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.Arrays;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author vinay
 */
public abstract class RESTServiceImpl implements RESTService, MaybeTransformer<RequestEvent, ResponseEvent> {

  private static final ResponseEvent DEFAULT_REPONSE_EVENT = new ResponseEvent();

  private String mOperationId;
  private String mServiceUniqueId;
  private Vertx mVertx;
  private boolean mEnable = false;
  private Logger mLog;
  private Scheduler mScheduler = Schedulers.io();
  private ResponseEvent mDefaultResponseEvent = DEFAULT_REPONSE_EVENT;
  private boolean mSecure = false;
  private String mAuthority;
  private Class<? extends RequestEvent> mRequestEventClass = RequestEvent.class;
  private MaybeTransformer<RequestEvent, RequestEvent> mPreProcessProcessors[];
  private MaybeTransformer<ResponseEvent, ResponseEvent> mPostProcessProcessors[];

  @StartService
  public void start() {
    if (mEnable) {
      getVertx()
              .eventBus()
              .<JsonObject>consumer(getServiceUniqueId())
              .toFlowable()
              .subscribeOn(getScheduler())
              .flatMapSingle(this::handleRequest)
              .doOnSubscribe((s) -> getLog().info(() -> String.format("%s[%s] service started", getServiceUniqueId(), Thread.currentThread().getName())))
              .doOnTerminate(() -> getLog().info(() -> String.format("%s[%s] service stopped", getServiceUniqueId(), Thread.currentThread().getName())))
              .doOnCancel(() -> getLog().info(() -> String.format("%s[%s] service stopped(cancel)", getServiceUniqueId(), Thread.currentThread().getName())))
              .doOnComplete(() -> getLog().info(() -> String.format("%s[%s] service stopped(complete)", getServiceUniqueId(), Thread.currentThread().getName())))
              .subscribe(resp -> getLog().trace(() -> resp.toString()));
    }
  }

  public Single<JsonObject> handleRequest(Message<JsonObject> pMessage) {
    return Single
            .just(pMessage)
            .map(m -> pMessage.body().mapTo(getRequestEventClass()))
            .flatMapMaybe(this::handleEvent)
            .toSingle(getDefaultResponseEvent())
            .map(resp -> JsonObject.mapFrom(resp))
            .doOnSuccess(resp -> pMessage.reply(resp))
            .doOnError(err -> {
              ResponseEvent resp = new ResponseEvent()
                      .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                      .setMediaType(MediaType.PLAIN_TEXT_UTF_8)
                      .setBody(ExceptionUtils.getMessage(err).getBytes());
              pMessage.reply(JsonObject.mapFrom(resp));
            })
            .doOnError(oerr -> getLog().error(() -> String.format("Process exception:[%s],Error:[%s]", getServiceUniqueId(), ExceptionUtils.getStackTrace(oerr))))
            .onErrorReturnItem(new JsonObject());
  }

  @Override
  public Maybe<ResponseEvent> handleEvent(RequestEvent pRequest) {

    return Maybe
            .just(pRequest)
            .compose(this::preProcess)
            .compose(this)
            .compose(this::postProcess);
  }

  public MaybeSource<RequestEvent> preProcess(Maybe<RequestEvent> pRequest) {

    if (getPreProcessProcessors() == null || getPreProcessProcessors().length == 0) {
      return pRequest;
    }

    return Arrays
            .stream(getPreProcessProcessors())
            .reduce(pRequest, (acc, p) -> acc.compose(p), (a, b) -> a);
  }

  @Override
  public MaybeSource<ResponseEvent> apply(Maybe<RequestEvent> pRequest) {
    return process(pRequest);
  }

  public abstract MaybeSource<ResponseEvent> process(Maybe<RequestEvent> pRequest);

  public Maybe<ResponseEvent> postProcess(Maybe<ResponseEvent> pResponse) {

    if (getPostProcessProcessors() == null || getPostProcessProcessors().length == 0) {
      return pResponse;
    }

    return Arrays
            .stream(getPostProcessProcessors())
            .reduce(pResponse, (acc, p) -> acc.compose(p), (a, b) -> a);
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

  public ResponseEvent getDefaultResponseEvent() {
    return mDefaultResponseEvent;
  }

  public void setDefaultResponseEvent(ResponseEvent pDefaultResponseEvent) {
    this.mDefaultResponseEvent = pDefaultResponseEvent;
  }

  @Override
  public boolean isSecure() {
    return mSecure;
  }

  public void setSecure(boolean pSecure) {
    this.mSecure = pSecure;
  }

  @Override
  public String getAuthority() {
    return mAuthority;
  }

  public void setAuthority(String pAuthority) {
    this.mAuthority = pAuthority;
  }

  public Class<? extends RequestEvent> getRequestEventClass() {
    return mRequestEventClass;
  }

  public void setRequestEventClass(Class<? extends RequestEvent> pRequestEventClass) {
    this.mRequestEventClass = pRequestEventClass;
  }

  public MaybeTransformer<RequestEvent, RequestEvent>[] getPreProcessProcessors() {
    return mPreProcessProcessors;
  }

  public void setPreProcessProcessors(MaybeTransformer<RequestEvent, RequestEvent>[] pPreProcessProcessors) {
    this.mPreProcessProcessors = pPreProcessProcessors;
  }

  public MaybeTransformer<ResponseEvent, ResponseEvent>[] getPostProcessProcessors() {
    return mPostProcessProcessors;
  }

  public void setPostProcessProcessors(MaybeTransformer<ResponseEvent, ResponseEvent>[] pPostProcessProcessors) {
    this.mPostProcessProcessors = pPostProcessProcessors;
  }

}
