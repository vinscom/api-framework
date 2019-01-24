package in.erail.service;

import com.google.common.net.MediaType;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.apache.logging.log4j.Logger;
import in.erail.glue.annotation.StartService;
import in.erail.model.Event;
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
public abstract class RESTServiceImpl implements RESTService, MaybeTransformer<Event, Event> {

  private String mOperationId;
  private String mServiceUniqueId;
  private Vertx mVertx;
  private boolean mEnable = false;
  private Logger mLog;
  private Scheduler mScheduler = Schedulers.io();
  private Event mDefaultEvent;
  private boolean mSecure = false;
  private String[] mAuthority;
  private Class<? extends RequestEvent> mRequestEventClass = RequestEvent.class;
  private Class<? extends ResponseEvent> mResponseEventClass = ResponseEvent.class;
  private MaybeTransformer<Event, Event> mPreProcessProcessors[];
  private MaybeTransformer<Event, Event> mPostProcessProcessors[];

  @StartService
  public void start() throws InstantiationException, IllegalAccessException {

    mDefaultEvent = new Event(getRequestEventClass().newInstance(), getResponseEventClass().newInstance());

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
            .map(this::createEvent)
            .flatMapMaybe(this::handleEvent)
            .toSingle(getDefaultEvent())
            .map(resp -> JsonObject.mapFrom(resp.getResponse()))
            .doOnSuccess(resp -> pMessage.reply(resp))
            .doOnError(err -> {
              ResponseEvent resp = getResponseEventClass().newInstance()
                      .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                      .setMediaType(MediaType.PLAIN_TEXT_UTF_8)
                      .setBody(ExceptionUtils.getMessage(err).getBytes());
              pMessage.reply(JsonObject.mapFrom(resp));
            })
            .doOnError(oerr -> getLog().error(() -> String.format("Process exception:[%s],Error:[%s]", getServiceUniqueId(), ExceptionUtils.getStackTrace(oerr))))
            .onErrorReturnItem(new JsonObject());
  }

  @Override
  public Maybe<Event> handleEvent(Event pEvent) {
    return Maybe
            .just(pEvent)
            .compose(composePipeline(getPreProcessProcessors()))
            .compose(this)
            .compose(composePipeline(getPostProcessProcessors()));
  }

  protected MaybeTransformer<Event, Event> composePipeline(MaybeTransformer<Event, Event>[] pProcessors) {

    if (pProcessors == null || pProcessors.length == 0) {
      return (Maybe<Event> pEvent) -> pEvent;
    }

    return (Maybe<Event> pEvent) -> Arrays
            .stream(pProcessors)
            .reduce(pEvent, (acc, p) -> acc.compose(p), (a, b) -> a);
  }

  @Override
  public final MaybeSource<Event> apply(Maybe<Event> pRequest) {
    return process(pRequest);
  }
  public abstract MaybeSource<Event> process(Maybe<Event> pEvent);

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

  public Event getDefaultEvent() {
    return mDefaultEvent;
  }

  public void setDefaultEvent(Event pDefaultEvent) {
    this.mDefaultEvent = pDefaultEvent;
  }

  @Override
  public boolean isSecure() {
    return mSecure;
  }

  public void setSecure(boolean pSecure) {
    this.mSecure = pSecure;
  }

  @Override
  public String[] getAuthority() {
    return mAuthority;
  }

  public void setAuthority(String[] pAuthority) {
    this.mAuthority = pAuthority;
  }

  @Override
  public Class<? extends RequestEvent> getRequestEventClass() {
    return mRequestEventClass;
  }

  public void setRequestEventClass(Class<? extends RequestEvent> pRequestEventClass) {
    this.mRequestEventClass = pRequestEventClass;
  }

  @Override
  public Class<? extends ResponseEvent> getResponseEventClass() {
    return mResponseEventClass;
  }

  public void setResponseEventClass(Class<? extends ResponseEvent> pResponseEventClass) {
    this.mResponseEventClass = pResponseEventClass;
  }

  public MaybeTransformer<Event, Event>[] getPreProcessProcessors() {
    return mPreProcessProcessors;
  }

  public void setPreProcessProcessors(MaybeTransformer<Event, Event>[] pPreProcessProcessors) {
    this.mPreProcessProcessors = pPreProcessProcessors;
  }

  public MaybeTransformer<Event, Event>[] getPostProcessProcessors() {
    return mPostProcessProcessors;
  }

  public void setPostProcessProcessors(MaybeTransformer<Event, Event>[] pPostProcessProcessors) {
    this.mPostProcessProcessors = pPostProcessProcessors;
  }

}
