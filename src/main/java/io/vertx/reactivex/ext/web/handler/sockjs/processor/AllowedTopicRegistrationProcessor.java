package io.vertx.reactivex.ext.web.handler.sockjs.processor;

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

  @Override
  public void process(BridgeEventContext pContext) {

    BridgeEvent event = pContext.getBridgeEvent();

    if (mAddressAllowedToRegister.isEmpty() && mAddressAllowedToRegisterRegex.isEmpty()) {
      event.complete(true);
      return;
    }

    if (!(matchAddress(pContext.getAddress()) || matchAddressRegex(pContext.getAddress()))) {
      getLog().debug(() -> "Registration failed:" + pContext.getAddress());
      event.fail("Can't subscribe to topic : " + pContext.getAddress());
    }

  }

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

}
