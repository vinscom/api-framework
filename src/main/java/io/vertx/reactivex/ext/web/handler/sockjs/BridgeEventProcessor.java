package io.vertx.reactivex.ext.web.handler.sockjs;

/**
 *
 * @author vinay
 */
public interface BridgeEventProcessor {

  void process(BridgeEventContext pContext);
}
