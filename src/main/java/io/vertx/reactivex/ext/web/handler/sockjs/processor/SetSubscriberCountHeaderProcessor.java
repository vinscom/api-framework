package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import com.google.common.base.Strings;
import in.erail.common.FramworkConstants;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventProcessor;
import io.vertx.reactivex.redis.RedisClient;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class SetSubscriberCountHeaderProcessor implements BridgeEventProcessor {

  private RedisClient mRedisClient;
  private boolean mEnable;
  private String mKeyPrefix;
  private String mCountHeaderFieldName;
  private Logger mLog;

  public RedisClient getRedisClient() {
    return mRedisClient;
  }

  public void setRedisClient(RedisClient pRedisClient) {
    this.mRedisClient = pRedisClient;
  }

  @Override
  public Single<BridgeEventContext> process(Single<BridgeEventContext> pContext) {

    if (!isEnable()) {
      return pContext;
    }

    return pContext
            .flatMap((ctx) -> {

              if (ctx.getBridgeEvent().failed()) {
                return Single.just(ctx);
              }

              if (Strings.isNullOrEmpty(ctx.getAddress())) {
                getLog().error(() -> String.format("[%s] Address can't empty", ctx.getId() != null ? ctx.getId() : ""));
                return Single.just(ctx);
              }
              
              getLog().debug(() -> String.format("[%s] Trying to fetch value of Redis:KEY:[%s]", ctx.getId(), ctx.getAddressKey()));

              return getRedisClient()
                      .rxGet(ctx.getAddressKey())
                      .map((count) -> {
                        
                        String headerValue = count;
                        
                        if (Strings.isNullOrEmpty(count)) {
                          getLog().debug(() -> String.format("[%s] Redis:KEY:[%s], Key value is null, Setting header value to 0", ctx.getId(), ctx.getAddressKey()));
                          headerValue = "0";
                        } else {
                          getLog().debug(() -> String.format("[%s] Found Redis:KEY:[%s],VALUE:[%s]", ctx.getId(), ctx.getAddressKey(), count));
                        }
                        
                        JsonObject rawMsg = ctx.getBridgeEvent().getRawMessage();
                        JsonObject headers = rawMsg.getJsonObject(FramworkConstants.SockJS.BRIDGE_EVENT_RAW_MESSAGE_HEADERS);
                        headers.put(getCountHeaderFieldName(), headerValue);
                        ctx.getBridgeEvent().setRawMessage(rawMsg);
                        return ctx;
                      })
                      .doOnError((err) -> {
                        getLog().error(String.format("[%s] Error getting value for Key[%s] from redis", ctx.getId(), ctx.getAddressKey()),err);
                      });

            });
  }

  public boolean isEnable() {
    return mEnable;
  }

  public void setEnable(boolean pEnable) {
    this.mEnable = pEnable;
  }

  public String getKeyPrefix() {
    return mKeyPrefix;
  }

  public void setKeyPrefix(String pKeyPrefix) {
    this.mKeyPrefix = pKeyPrefix;
  }

  public String getCountHeaderFieldName() {
    return mCountHeaderFieldName;
  }

  public void setCountHeaderFieldName(String pCountHeaderFieldName) {
    this.mCountHeaderFieldName = pCountHeaderFieldName;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

}
