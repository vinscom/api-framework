package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import com.google.common.base.Strings;
import io.reactivex.Single;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class LoadAddressKeyProcessor implements BridgeEventProcessor {

  private String mKeyPrefix;
  private Logger mLog;

  @Override
  public Single<BridgeEventContext> process(Single<BridgeEventContext> pContext) {

    return pContext
            .map((ctx) -> {

              if(ctx.getBridgeEvent().future().failed()){
                return ctx;
              }
              
              if (Strings.isNullOrEmpty(ctx.getAddress())) {
                getLog().error(() -> String.format("[%s] Address can't empty", ctx.getId() != null ? ctx.getId() : ""));
                return ctx;
              }

              ctx.setAddressKey(getKeyPrefix() + ctx.getAddress());
              getLog().debug(() -> String.format("[%s] AddressKey:[%s]", ctx.getId(), ctx.getAddressKey()));

              return ctx;
            });

  }

  public String getKeyPrefix() {
    return mKeyPrefix;
  }

  public void setKeyPrefix(String pKeyPrefix) {
    this.mKeyPrefix = pKeyPrefix;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

}
