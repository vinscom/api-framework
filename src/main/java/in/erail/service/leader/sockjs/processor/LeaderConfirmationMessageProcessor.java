package in.erail.service.leader.sockjs.processor;

import com.google.common.base.Strings;
import in.erail.common.FrameworkConstants;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.processor.BridgeEventProcessor;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class LeaderConfirmationMessageProcessor implements BridgeEventProcessor {

  private String mSendMessageHeaderConfirmMsgFieldName;
  private String mSendMessageHeaderSessionFieldName;
  private Logger mLog;

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

  @Override
  public Single<BridgeEventContext> process(Single<BridgeEventContext> pContext) {
    return pContext
            .map((ctx) -> {

              if (ctx.getBridgeEvent().failed()) {
                return ctx;
              }

              if (Strings.isNullOrEmpty(ctx.getAddress())) {
                getLog().error(() -> String.format("[%s] Address can't empty", ctx.getId() != null ? ctx.getId() : ""));
                return ctx;
              }

              JsonObject rawMsg = ctx.getBridgeEvent().getRawMessage();
              JsonObject headers = rawMsg.getJsonObject(FrameworkConstants.SockJS.BRIDGE_EVENT_RAW_MESSAGE_HEADERS);

              if (headers != null && headers.containsKey(getSendMessageHeaderConfirmMsgFieldName())) {
                //Only add session on confirmation messages
                String session = ctx.getBridgeEvent().socket().writeHandlerID();
                getLog().debug(() -> String.format("[%s] Header:[%s] found in message", ctx.getId(), getSendMessageHeaderConfirmMsgFieldName()));
                getLog().debug(() -> String.format("[%s] Setting Header:[%s] to Session:[%s]", ctx.getId(), getSendMessageHeaderConfirmMsgFieldName(), session));
                headers.put(getSendMessageHeaderSessionFieldName(), session);
              } else {
                getLog().debug(() -> String.format("[%s] Header:[%s] not found", ctx.getId(), getSendMessageHeaderConfirmMsgFieldName()));
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
