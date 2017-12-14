package in.erail.service.leader.sockjs.processor;

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

  @Override
  public void process(BridgeEventContext pContext) {
    if (pContext.getBridgeEvent().failed()) {
      return;
    }

    sendBridgeEventUpdate(pContext.getBridgeEvent().type(), pContext.getAddress(), pContext.getBridgeEvent().socket().writeHandlerID());
  }

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

}
