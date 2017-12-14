package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import in.erail.common.FramworkConstants;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventProcessor;

/**
 *
 * @author vinay
 */
public class LoadAddressProcessor implements BridgeEventProcessor {

  @Override
  public void process(BridgeEventContext pContext) {
    
    JsonObject rawMessage = pContext.getBridgeEvent().getRawMessage();
    if (rawMessage != null) {
      pContext.setAddress(rawMessage.getString(FramworkConstants.SockJS.BRIDGE_EVENT_RAW_MESSAGE_ADDRESS));
    }

  }
  
}
