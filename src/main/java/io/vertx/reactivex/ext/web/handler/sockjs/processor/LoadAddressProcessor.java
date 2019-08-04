package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import in.erail.common.FrameworkConstants;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class LoadAddressProcessor implements BridgeEventProcessor {

  private Logger mLog;

  @Override
  public Single<BridgeEventContext> process(Single<BridgeEventContext> pContext) {

    return pContext
            .map((ctx) -> {
              
              if(ctx.getBridgeEvent().future().failed()){
                return ctx;
              }
              
              JsonObject rawMessage = ctx.getBridgeEvent().getRawMessage();
              if (rawMessage != null) {
                ctx.setAddress(rawMessage.getString(FrameworkConstants.SockJS.BRIDGE_EVENT_RAW_MESSAGE_ADDRESS));
                getLog().debug(() -> String.format("[%s] Address:[%s]", ctx.getId(), ctx.getAddress()));
              }
              return ctx;
            });

  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

}
