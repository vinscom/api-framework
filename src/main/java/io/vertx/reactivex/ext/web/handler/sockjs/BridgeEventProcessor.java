package io.vertx.reactivex.ext.web.handler.sockjs;

import io.reactivex.Single;

/**
 *
 * @author vinay
 */
public interface BridgeEventProcessor {

  Single<BridgeEventContext> process(Single<BridgeEventContext> pContext);
}
