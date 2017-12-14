package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import com.google.common.base.Strings;
import in.erail.common.FramworkConstants;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventProcessor;
import io.vertx.reactivex.redis.RedisClient;

/**
 *
 * @author vinay
 */
public class SetSubscriberCountHeaderProcessor implements BridgeEventProcessor {

  private RedisClient mRedisClient;
  private boolean mEnable;

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

              JsonObject rawMsg = ctx.getBridgeEvent().getRawMessage();
              JsonObject headers = rawMsg.getJsonObject(FramworkConstants.SockJS.BRIDGE_EVENT_RAW_MESSAGE_HEADERS);

              return getRedisClient()
                      .rxGet(ctx.getAddress())
                      .map((count) -> {
                        if (Strings.isNullOrEmpty(count)) {
                          return ctx;
                        }
                        headers.put("count", count);
                        ctx.getBridgeEvent().setRawMessage(rawMsg);
                        return ctx;
                      });

            });
  }

  public boolean isEnable() {
    return mEnable;
  }

  public void setEnable(boolean pEnable) {
    this.mEnable = pEnable;
  }

}
