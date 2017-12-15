package io.vertx.reactivex.ext.web.handler.sockjs;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author vinay
 */
public class BridgeEventContext {

  private BridgeEvent mBridgeEvent;
  private String mAddress;

  public BridgeEvent getBridgeEvent() {
    return mBridgeEvent;
  }

  public void setBridgeEvent(BridgeEvent pBridgeEvent) {
    this.mBridgeEvent = pBridgeEvent;
  }

  public String getAddress() {
    return mAddress;
  }

  public void setAddress(String pAddress) {
    this.mAddress = pAddress;
  }
  
}
