package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import io.reactivex.Single;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;

/**
 *
 * @author vinay
 */
public interface BridgeEventProcessor {

  Single<BridgeEventContext> process(Single<BridgeEventContext> pContext);
}
