package in.erail.service.leader.sockjs.processor;

import in.erail.common.FramworkConstants;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventProcessor;

/**
 *
 * @author vinay
 */
public class LeaderConfirmationMessageProcessor implements BridgeEventProcessor {

  private String mSendMessageHeaderConfirmMsgFieldName;
  private String mSendMessageHeaderSessionFieldName;

  @Override
  public void process(BridgeEventContext pContext) {
    if (pContext.getBridgeEvent().failed()) {
      return;
    }

    JsonObject rawMsg = pContext.getBridgeEvent().getRawMessage();
    JsonObject headers = rawMsg.getJsonObject(FramworkConstants.SockJS.BRIDGE_EVENT_RAW_MESSAGE_HEADERS);

    if (headers.containsKey(getSendMessageHeaderConfirmMsgFieldName())) {
      //Only add session on confirmation messages
      headers.put(getSendMessageHeaderSessionFieldName(), pContext.getBridgeEvent().socket().writeHandlerID());
      pContext.getBridgeEvent().setRawMessage(rawMsg);
    }

  }

  public String getSendMessageHeaderConfirmMsgFieldName() {
    return mSendMessageHeaderConfirmMsgFieldName;
  }

  public void setSendMessageHeaderConfirmMsgFieldName(String pSendMessageHeaderConfirmMsgFieldName) {
    this.mSendMessageHeaderConfirmMsgFieldName = pSendMessageHeaderConfirmMsgFieldName;
  }

  public String getSendMessageHeaderSessionFieldName() {
    return mSendMessageHeaderSessionFieldName;
  }

  public void setSendMessageHeaderSessionFieldName(String pSendMessageHeaderSessionFieldName) {
    this.mSendMessageHeaderSessionFieldName = pSendMessageHeaderSessionFieldName;
  }

}
