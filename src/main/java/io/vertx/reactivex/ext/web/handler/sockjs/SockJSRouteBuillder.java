package io.vertx.reactivex.ext.web.handler.sockjs;

import in.erail.route.AbstractRouterBuilderImpl;
import io.vertx.core.Handler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.handler.sockjs.Transport;
import io.vertx.reactivex.ext.web.Router;

/**
 *
 * @author vinay
 */
public class SockJSRouteBuillder extends AbstractRouterBuilderImpl {

  private SockJSBridgeOptions mBridgeOptions;
  private Handler<BridgeEvent> mBridgeEventHandler;

  public SockJSBridgeOptions getBridgeOptions() {
    return mBridgeOptions;
  }

  public void setBridgeOptions(SockJSBridgeOptions pBridgeOptions) {
    this.mBridgeOptions = pBridgeOptions;
  }

  public Handler<BridgeEvent> getBridgeEventHandler() {
    return mBridgeEventHandler;
  }

  public void setBridgeEventHandler(Handler<BridgeEvent> pBridgeEventHandler) {
    this.mBridgeEventHandler = pBridgeEventHandler;
  }

  @Override
  public Router getRouter(Router pRouter) {
    // Eventbus handle
    SockJSHandlerOptions sockJSHandlerOptions = (new SockJSHandlerOptions())
            .addDisabledTransport(Transport.EVENT_SOURCE.toString())
            .addDisabledTransport(Transport.HTML_FILE.toString())
            .addDisabledTransport(Transport.JSON_P.toString())
            .addDisabledTransport(Transport.XHR.toString())
            .setInsertJSESSIONID(false);

    return SockJSHandler
            .create(getVertx(), sockJSHandlerOptions)
            .bridge(getBridgeOptions(), getBridgeEventHandler());
  }

}
