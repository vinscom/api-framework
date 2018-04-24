package io.vertx.reactivex.ext.web.handler.sockjs;

import com.codahale.metrics.Meter;
import io.vertx.reactivex.ext.web.handler.sockjs.processor.BridgeEventProcessor;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.Handler;
import java.util.UUID;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class BridgeEventHandler implements Handler<BridgeEvent> {

  private Logger mLog;
  private BridgeEventProcessor[] mPublishProcessors;
  private BridgeEventProcessor[] mReceiveProcessors;
  private BridgeEventProcessor[] mRegisterProcessors;
  private BridgeEventProcessor[] mSendProcessors;
  private BridgeEventProcessor[] mSocketClosedProcessors;
  private BridgeEventProcessor[] mSocketCreatedProcessors;
  private BridgeEventProcessor[] mSocketIdleProcessors;
  private BridgeEventProcessor[] mSoketPingProcessors;
  private BridgeEventProcessor[] mUnregisterProcessors;
  private Meter mMetricsBridgeEventSend;
  private Meter mMetricsBridgeEventPublish;
  private Meter mMetricsBridgeEventReceive;
  private Meter mMetricsBridgeEventRegister;
  private Meter mMetricsBridgeEventUnregister;

  @Override
  public void handle(BridgeEvent pEvent) {

    switch (pEvent.type()) {
      case PUBLISH:
        getMetricsBridgeEventPublish().mark();
        process(getPublishProcessors(), pEvent);
        break;
      case RECEIVE:
        getMetricsBridgeEventReceive().mark();
        process(getReceiveProcessors(), pEvent);
        break;
      case REGISTER:
        getMetricsBridgeEventRegister().mark();
        process(getRegisterProcessors(), pEvent);
        break;
      case SEND:
        getMetricsBridgeEventSend().mark();
        process(getSendProcessors(), pEvent);
        break;
      case SOCKET_CLOSED:
        process(getSocketClosedProcessors(), pEvent);
        break;
      case SOCKET_CREATED:
        process(getSocketCreatedProcessors(), pEvent);
        break;
      case SOCKET_IDLE:
        process(getSocketIdleProcessors(), pEvent);
        break;
      case SOCKET_PING:
        process(getSoketPingProcessors(), pEvent);
        break;
      case UNREGISTER:
        getMetricsBridgeEventUnregister().mark();
        process(getUnregisterProcessors(), pEvent);
        break;
    }
  }

  protected void process(BridgeEventProcessor[] pProcessors, BridgeEvent pEvent) {

    if (pProcessors == null || pProcessors.length == 0) {
      pEvent.complete(true);
      return;
    }

    BridgeEventContext ctx = new BridgeEventContext();
    ctx.setBridgeEvent(pEvent);

    if (getLog().isDebugEnabled()) {
      ctx.setId(UUID.randomUUID().toString());
    }

    Observable
            .fromArray(pProcessors)
            .reduce(Single.just(ctx), (acc, processor) -> processor.process(acc))
            .flatMap((context) -> context)
            .doFinally(() -> {
              if (ctx.getBridgeEvent().failed()) {
                getLog().debug(() -> String.format("[%s] BridgeEvent Failed: [%s]", ctx.getId(), ctx.getBridgeEvent().getRawMessage()));
                return;
              }
              getLog().debug(() -> String.format("[%s] BridgeEvent Success [%s]", ctx.getId(), ctx.getBridgeEvent().getRawMessage()));
              ctx.getBridgeEvent().complete(true);
            })
            .subscribe();

  }

  public BridgeEventProcessor[] getPublishProcessors() {
    return mPublishProcessors;
  }

  public void setPublishProcessors(BridgeEventProcessor[] pPublishProcessors) {
    this.mPublishProcessors = pPublishProcessors;
  }

  public BridgeEventProcessor[] getReceiveProcessors() {
    return mReceiveProcessors;
  }

  public void setReceiveProcessors(BridgeEventProcessor[] pReceiveProcessors) {
    this.mReceiveProcessors = pReceiveProcessors;
  }

  public BridgeEventProcessor[] getRegisterProcessors() {
    return mRegisterProcessors;
  }

  public void setRegisterProcessors(BridgeEventProcessor[] pRegisterProcessors) {
    this.mRegisterProcessors = pRegisterProcessors;
  }

  public BridgeEventProcessor[] getSendProcessors() {
    return mSendProcessors;
  }

  public void setSendProcessors(BridgeEventProcessor[] pSendProcessors) {
    this.mSendProcessors = pSendProcessors;
  }

  public BridgeEventProcessor[] getSocketClosedProcessors() {
    return mSocketClosedProcessors;
  }

  public void setSocketClosedProcessors(BridgeEventProcessor[] pSocketClosedProcessors) {
    this.mSocketClosedProcessors = pSocketClosedProcessors;
  }

  public BridgeEventProcessor[] getSocketCreatedProcessors() {
    return mSocketCreatedProcessors;
  }

  public void setSocketCreatedProcessors(BridgeEventProcessor[] pSocketCreatedProcessors) {
    this.mSocketCreatedProcessors = pSocketCreatedProcessors;
  }

  public BridgeEventProcessor[] getSocketIdleProcessors() {
    return mSocketIdleProcessors;
  }

  public void setSocketIdleProcessors(BridgeEventProcessor[] pSocketIdleProcessors) {
    this.mSocketIdleProcessors = pSocketIdleProcessors;
  }

  public BridgeEventProcessor[] getSoketPingProcessors() {
    return mSoketPingProcessors;
  }

  public void setSoketPingProcessors(BridgeEventProcessor[] pSoketPingProcessors) {
    this.mSoketPingProcessors = pSoketPingProcessors;
  }

  public BridgeEventProcessor[] getUnregisterProcessors() {
    return mUnregisterProcessors;
  }

  public void setUnregisterProcessors(BridgeEventProcessor[] pUnregisterProcessors) {
    this.mUnregisterProcessors = pUnregisterProcessors;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public Meter getMetricsBridgeEventSend() {
    return mMetricsBridgeEventSend;
  }

  public void setMetricsBridgeEventSend(Meter pMetricsBridgeEventSend) {
    this.mMetricsBridgeEventSend = pMetricsBridgeEventSend;
  }

  public Meter getMetricsBridgeEventPublish() {
    return mMetricsBridgeEventPublish;
  }

  public void setMetricsBridgeEventPublish(Meter pMetricsBridgeEventPublish) {
    this.mMetricsBridgeEventPublish = pMetricsBridgeEventPublish;
  }

  public Meter getMetricsBridgeEventReceive() {
    return mMetricsBridgeEventReceive;
  }

  public void setMetricsBridgeEventReceive(Meter pMetricsBridgeEventReceive) {
    this.mMetricsBridgeEventReceive = pMetricsBridgeEventReceive;
  }

  public Meter getMetricsBridgeEventRegister() {
    return mMetricsBridgeEventRegister;
  }

  public void setMetricsBridgeEventRegister(Meter pMetricsBridgeEventRegister) {
    this.mMetricsBridgeEventRegister = pMetricsBridgeEventRegister;
  }

  public Meter getMetricsBridgeEventUnregister() {
    return mMetricsBridgeEventUnregister;
  }

  public void setMetricsBridgeEventUnregister(Meter pMetricsBridgeEventUnregister) {
    this.mMetricsBridgeEventUnregister = pMetricsBridgeEventUnregister;
  }

}
