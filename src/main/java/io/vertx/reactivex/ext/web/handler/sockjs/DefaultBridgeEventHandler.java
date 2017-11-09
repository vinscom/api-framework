package io.vertx.reactivex.ext.web.handler.sockjs;

import io.vertx.core.Handler;

/**
 *
 * @author vinay
 */
public class DefaultBridgeEventHandler implements Handler<BridgeEvent> {

  @Override
  public void handle(BridgeEvent pEvent) {
    pEvent.complete(true);
  }
  
}
