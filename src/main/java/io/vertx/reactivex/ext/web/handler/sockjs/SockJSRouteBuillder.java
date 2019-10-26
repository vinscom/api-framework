package io.vertx.reactivex.ext.web.handler.sockjs;

import in.erail.glue.annotation.StartService;
import in.erail.route.AbstractRouterBuilderImpl;
import io.vertx.core.Handler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.handler.sockjs.Transport;
import io.vertx.reactivex.ext.web.Router;

/**
 *
 * @author vinay
 */
public class SockJSRouteBuillder  extends AbstractRouterBuilderImpl {

  private BridgeOptions mBridgeOptions;
  private Handler<BridgeEvent> mBridgeEventHandler;
  private Router mSockJSRouter;

  @StartService
  public void start() {

    // Eventbus handle
    SockJSHandlerOptions sockJSHandlerOptions = (new SockJSHandlerOptions())
            .addDisabledTransport(Transport.EVENT_SOURCE.toString())
            .addDisabledTransport(Transport.HTML_FILE.toString())
            .addDisabledTransport(Transport.JSON_P.toString())
            .addDisabledTransport(Transport.XHR.toString())
            .setInsertJSESSIONID(false);

    mSockJSRouter = SockJSHandler
            .create(getVertx(), sockJSHandlerOptions)
            .bridge(getBridgeOptions(), getBridgeEventHandler());
  }

  public BridgeOptions getBridgeOptions() {
    return mBridgeOptions;
  }

  public void setBridgeOptions(BridgeOptions pBridgeOptions) {
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
    return mSockJSRouter;
  }

}
