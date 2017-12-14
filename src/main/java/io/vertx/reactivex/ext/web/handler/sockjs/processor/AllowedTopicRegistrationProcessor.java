package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import com.google.common.base.Strings;
import io.reactivex.Single;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventProcessor;
import java.util.List;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class AllowedTopicRegistrationProcessor implements BridgeEventProcessor {

  private Logger mLog;
  private List<String> mAddressAllowedToRegister;
  private List<String> mAddressAllowedToRegisterRegex;

  private boolean matchAddress(String pAddress) {
    return mAddressAllowedToRegister
            .stream()
            .anyMatch((allowedAddress) -> (pAddress.equals(allowedAddress)));
  }

  private boolean matchAddressRegex(String pAddress) {
    return mAddressAllowedToRegisterRegex
            .stream()
            .anyMatch((allowedAddress) -> (pAddress.matches(allowedAddress)));
  }

  public List<String> getAddressAllowedToRegister() {
    return mAddressAllowedToRegister;
  }

  public void setAddressAllowedToRegister(List<String> pAddressAllowedToRegister) {
    this.mAddressAllowedToRegister = pAddressAllowedToRegister;
  }

  public List<String> getAddressAllowedToRegisterRegex() {
    return mAddressAllowedToRegisterRegex;
  }

  public void setAddressAllowedToRegisterRegex(List<String> pAddressAllowedToRegisterRegex) {
    this.mAddressAllowedToRegisterRegex = pAddressAllowedToRegisterRegex;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  @Override
  public Single<BridgeEventContext> process(Single<BridgeEventContext> pContext) {
    return pContext
            .map((ctx) -> {
              if (mAddressAllowedToRegister.isEmpty() && mAddressAllowedToRegisterRegex.isEmpty()) {
                return ctx;
              }

              if (Strings.isNullOrEmpty(ctx.getAddress())) {
                getLog().error("Address missing");
                return ctx;
              }

              if (!(matchAddress(ctx.getAddress()) || matchAddressRegex(ctx.getAddress()))) {
                getLog().debug(() -> "Registration failed:" + ctx.getAddress());
                ctx.getBridgeEvent().fail("Can't subscribe to topic : " + ctx.getAddress());
              }
              return ctx;
            });
  }

}
