package io.vertx.reactivex.ext.web.handler.sockjs;

import in.erail.common.FramworkConstants;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author vinay
 */
public class DefaultBridgeEventHandler implements Handler<BridgeEvent> {

  @Override
  public void handle(BridgeEvent pEvent) {

    JsonObject rawMessage = pEvent.getRawMessage();
    String address = null;

    if (rawMessage != null) {
      address = rawMessage.getString(FramworkConstants.SockJS.BRIDGE_EVENT_RAW_MESSAGE_ADDRESS);
    }

    switch (pEvent.type()) {
      case PUBLISH:
        handlePublish(address, pEvent);
        break;
      case RECEIVE:
        handleRecieve(address, pEvent);
        break;
      case REGISTER:
        handleRegister(address, pEvent);
        break;
      case SEND:
        handleSend(address, pEvent);
        break;
      case SOCKET_CLOSED:
        handleSocketClose(pEvent);
        break;
      case SOCKET_CREATED:
        handleSocketCreated(pEvent);
        break;
      case SOCKET_IDLE:
        handleSocketIdle(pEvent);
        break;
      case SOCKET_PING:
        handleSocketPing(pEvent);
        break;
      case UNREGISTER:
        handleUnregister(address, pEvent);
        break;
    }

  }

  public void handlePublish(String pAddress, BridgeEvent pEvent) {
    pEvent.complete(true);
  }

  public void handleRecieve(String pAddress, BridgeEvent pEvent) {
    pEvent.complete(true);
  }

  public void handleRegister(String pAddress, BridgeEvent pEvent) {
    pEvent.complete(true);
  }

  public void handleSend(String pAddress, BridgeEvent pEvent) {
    pEvent.complete(true);
  }

  public void handleSocketClose(BridgeEvent pEvent) {
    pEvent.complete(true);
  }

  public void handleSocketCreated(BridgeEvent pEvent) {
    pEvent.complete(true);
  }

  public void handleSocketIdle(BridgeEvent pEvent) {
    pEvent.complete(true);
  }

  public void handleSocketPing(BridgeEvent pEvent) {
    pEvent.complete(true);
  }

  public void handleUnregister(String pAddress, BridgeEvent pEvent) {
    pEvent.complete(true);
  }

}
