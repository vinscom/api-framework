package in.erail.service.leader.sockjs.processor;

import io.reactivex.Single;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventProcessor;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventUpdate;

/**
 *
 * @author vinay
 */
public class LeaderProcessor implements BridgeEventProcessor {

  private String mBridgeEventUpdateTopicName;
  private Vertx mVertx;

  public void sendBridgeEventUpdate(BridgeEventType pType, String pAddress, String pSession) {
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

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  @Override
  public Single<BridgeEventContext> process(Single<BridgeEventContext> pContext) {
    return pContext
            .doOnSuccess((ctx) -> {
              if (ctx.getBridgeEvent().failed()) {
                return;
              }
              sendBridgeEventUpdate(ctx.getBridgeEvent().type(), ctx.getAddress(), ctx.getBridgeEvent().socket().writeHandlerID());
            });
  }

}
