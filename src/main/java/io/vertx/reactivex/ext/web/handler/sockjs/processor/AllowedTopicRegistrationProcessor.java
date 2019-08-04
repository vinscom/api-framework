package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;

import io.reactivex.Single;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;

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
              
              if(ctx.getBridgeEvent().future().failed()){
                return ctx;
              }
              
              if (mAddressAllowedToRegister.isEmpty() && mAddressAllowedToRegisterRegex.isEmpty()) {
                getLog().debug(() -> String.format("[%s] No Access Restriction", ctx.getId() != null ? ctx.getId() : ""));
                return ctx;
              }

              if (Strings.isNullOrEmpty(ctx.getAddress())) {
                getLog().error(() -> String.format("[%s] Address can't empty", ctx.getId() != null ? ctx.getId() : ""));
                return ctx;
              }

              if (!(matchAddress(ctx.getAddress()) || matchAddressRegex(ctx.getAddress()))) {
                getLog().debug(() -> String.format("[%s] Registration failed:[%s]", ctx.getId(), ctx.getAddress()));
                ctx.getBridgeEvent().fail("Can't subscribe to topic : " + ctx.getAddress());
              } else {
                getLog().debug(() -> String.format("[%s] Registration Allowed:[%s]", ctx.getId(), ctx.getAddress()));
              }
              
              return ctx;
            });
  }

}
