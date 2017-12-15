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
                getLog().error(() -> "Address can't empty");
                return Single.just(ctx);
              }

              JsonObject rawMsg = ctx.getBridgeEvent().getRawMessage();
              JsonObject headers = rawMsg.getJsonObject(FramworkConstants.SockJS.BRIDGE_EVENT_RAW_MESSAGE_HEADERS);

              return getRedisClient()
                      .rxGet(getKeyPrefix() + ctx.getAddress())
                      .map((count) -> {

                        getLog().debug(() -> String.format("Redis:KEY:[%s],VALUE:[%s]", getKeyPrefix() + ctx.getAddress(), count));

                        if (Strings.isNullOrEmpty(count)) {
                          return ctx;
                        }
                        headers.put(getCountHeaderFieldName(), count);
                        ctx.getBridgeEvent().setRawMessage(rawMsg);
                        return ctx;
                      })
                      .doOnError((err) -> {
                        getLog().error(() -> String.format("Error getting value for Key[%s] from redis: [%s]", getKeyPrefix() + ctx.getAddress(), err.getCause().getMessage()));
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
