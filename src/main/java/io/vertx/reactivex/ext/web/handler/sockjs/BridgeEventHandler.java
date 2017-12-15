package io.vertx.reactivex.ext.web.handler.sockjs;

import in.erail.glue.component.ServiceArray;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.Handler;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class BridgeEventHandler implements Handler<BridgeEvent> {

  private Logger mLog;
  private ServiceArray mPublishProcessors;
  private ServiceArray mReceiveProcessors;
  private ServiceArray mRegisterProcessors;
  private ServiceArray mSendProcessors;
  private ServiceArray mSocketClosedProcessors;
  private ServiceArray mSocketCreatedProcessors;
  private ServiceArray mSocketIdleProcessors;
  private ServiceArray mSoketPingProcessors;
  private ServiceArray mUnregisterProcessors;

  @Override
  public void handle(BridgeEvent pEvent) {

    switch (pEvent.type()) {
      case PUBLISH:
        process(getPublishProcessors(), pEvent);
        break;
      case RECEIVE:
        process(getReceiveProcessors(), pEvent);
        break;
      case REGISTER:
        process(getRegisterProcessors(), pEvent);
        break;
      case SEND:
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
        process(getUnregisterProcessors(), pEvent);
        break;
    }
  }

  protected void process(ServiceArray pProcessors, BridgeEvent pEvent) {
    if (pProcessors.getServices() == null || pProcessors.getServices().isEmpty()) {
      pEvent.complete(true);
      return;
    }

    BridgeEventContext ctx = new BridgeEventContext();
    ctx.setBridgeEvent(pEvent);

    Observable
            .fromIterable(pProcessors.getServices())
            .reduce(Single.just(ctx), (acc, processor) -> {
              getLog().trace(() -> String.format("Processor [%s]", processor.getClass().getCanonicalName()));
              BridgeEventProcessor p = (BridgeEventProcessor) processor;
              return p.process(acc);
            })
            .doOnSuccess((t) -> {
              getLog().debug(() -> String.format("Reduce Success"));
            })
            .flatMap((context) -> context)
            .doOnSuccess((t) -> {
              getLog().debug(() -> String.format("Flatmap Success"));
            })
            .doFinally(() -> {
              getLog().debug(() -> String.format("Finally Called"));
              if (ctx.getBridgeEvent().failed()) {
                getLog().debug(() -> String.format("BridgeEvent Failed"));
                return;
              }
              getLog().trace(() -> String.format("BridgeEvent Success"));
              ctx.getBridgeEvent().complete(true);
            })
            .subscribe();

  }

  public ServiceArray getPublishProcessors() {
    return mPublishProcessors;
  }

  public void setPublishProcessors(ServiceArray pPublishProcessors) {
    this.mPublishProcessors = pPublishProcessors;
  }

  public ServiceArray getReceiveProcessors() {
    return mReceiveProcessors;
  }

  public void setReceiveProcessors(ServiceArray pReceiveProcessors) {
    this.mReceiveProcessors = pReceiveProcessors;
  }

  public ServiceArray getRegisterProcessors() {
    return mRegisterProcessors;
  }

  public void setRegisterProcessors(ServiceArray pRegisterProcessors) {
    this.mRegisterProcessors = pRegisterProcessors;
  }

  public ServiceArray getSendProcessors() {
    return mSendProcessors;
  }

  public void setSendProcessors(ServiceArray pSendProcessors) {
    this.mSendProcessors = pSendProcessors;
  }

  public ServiceArray getSocketClosedProcessors() {
    return mSocketClosedProcessors;
  }

  public void setSocketClosedProcessors(ServiceArray pSocketClosedProcessors) {
    this.mSocketClosedProcessors = pSocketClosedProcessors;
  }

  public ServiceArray getSocketCreatedProcessors() {
    return mSocketCreatedProcessors;
  }

  public void setSocketCreatedProcessors(ServiceArray pSocketCreatedProcessors) {
    this.mSocketCreatedProcessors = pSocketCreatedProcessors;
  }

  public ServiceArray getSocketIdleProcessors() {
    return mSocketIdleProcessors;
  }

  public void setSocketIdleProcessors(ServiceArray pSocketIdleProcessors) {
    this.mSocketIdleProcessors = pSocketIdleProcessors;
  }

  public ServiceArray getSoketPingProcessors() {
    return mSoketPingProcessors;
  }

  public void setSoketPingProcessors(ServiceArray pSoketPingProcessors) {
    this.mSoketPingProcessors = pSoketPingProcessors;
  }

  public ServiceArray getUnregisterProcessors() {
    return mUnregisterProcessors;
  }

  public void setUnregisterProcessors(ServiceArray pUnregisterProcessors) {
    this.mUnregisterProcessors = pUnregisterProcessors;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

}
