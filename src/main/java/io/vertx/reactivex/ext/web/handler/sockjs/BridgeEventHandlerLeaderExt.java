package io.vertx.reactivex.ext.web.handler.sockjs;

import io.vertx.ext.bridge.BridgeEventType;

/**
 *
 * @author vinay
 */
public class BridgeEventHandlerLeaderExt extends BridgeEventHandler {

  private String mBridgeEventUpdateTopicName;

  @Override
  public void handleRegister(String pAddress, BridgeEvent pEvent) {
    super.handleRegister(pAddress, pEvent);

    if (pEvent.isComplete()) {
      sendBridgeEventUpdate(pEvent.type(), pAddress, pEvent.socket().writeHandlerID());
    }
  }

  @Override
  public void handleUnregister(String pAddress, BridgeEvent pEvent) {
    super.handleUnregister(pAddress, pEvent);

    if (pEvent.isComplete()) {
      sendBridgeEventUpdate(pEvent.type(), pAddress, pEvent.socket().writeHandlerID());
    }
  }

  public void sendBridgeEventUpdate(BridgeEventType pType, String pAddress,String pSession) {
    BridgeEventUpdate beu = new BridgeEventUpdate();
    beu.setAddress(pAddress);
    beu.setType(pType);
    beu.setSession(pSession);  
    getVertx().eventBus().send(getBridgeEventUpdateTopicName(), beu.toJson());
  }

  public String getBridgeEventUpdateTopicName() {
    return mBridgeEventUpdateTopicName;
  }

  public void setBridgeEventUpdateTopicName(String pBridgeEventUpdateTopicName) {
    this.mBridgeEventUpdateTopicName = pBridgeEventUpdateTopicName;
  }

}
