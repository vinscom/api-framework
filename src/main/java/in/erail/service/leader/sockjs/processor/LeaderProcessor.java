package in.erail.service.leader.sockjs.processor;

import com.google.common.base.Strings;
import io.reactivex.Single;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.processor.BridgeEventProcessor;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventUpdate;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class LeaderProcessor implements BridgeEventProcessor {

  private String mBridgeEventUpdateTopicName;
  private Vertx mVertx;
  private Logger mLog;

  public BridgeEventUpdate createBridgeEventUpdate(BridgeEventType pType, String pAddress, String pSession) {
    BridgeEventUpdate beu = new BridgeEventUpdate();
    beu.setAddress(pAddress);
    beu.setType(pType);
    beu.setSession(pSession);
    return beu;
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
                getLog().debug(() -> String.format("[%s] Failed Event", ctx.getId()));
                return;
              }

              if (Strings.isNullOrEmpty(ctx.getAddress())) {
                getLog().error(() -> String.format("[%s] Address can't empty", ctx.getId() != null ? ctx.getId() : ""));
                return;
              }

              BridgeEventUpdate beu = createBridgeEventUpdate(ctx.getBridgeEvent().type(), ctx.getAddress(), ctx.getBridgeEvent().socket().writeHandlerID());
              getLog().debug(() -> String.format("[%s] Sending Message:[%s] to Topic:[%s]", ctx.getId(), beu.toJson().toString(), getBridgeEventUpdateTopicName()));
              getVertx().eventBus().send(getBridgeEventUpdateTopicName(), beu.toJson());
            });
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

}
