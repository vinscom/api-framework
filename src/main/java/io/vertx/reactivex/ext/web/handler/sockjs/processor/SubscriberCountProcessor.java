package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import io.reactivex.Single;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventProcessor;
import io.vertx.reactivex.redis.RedisClient;

/**
 *
 * @author vinay
 */
public class SubscriberCountProcessor implements BridgeEventProcessor {

  private RedisClient mRedisClient;
  private long mCounterExpire;
  private boolean mEnable;

  public RedisClient getRedisClient() {
    return mRedisClient;
  }

  public void setRedisClient(RedisClient pRedisClient) {
    this.mRedisClient = pRedisClient;
  }

  public long getCounterExpire() {
    return mCounterExpire;
  }

  public void setCounterExpire(long pCounterExpire) {
    this.mCounterExpire = pCounterExpire;
  }

  @Override
  public Single<BridgeEventContext> process(Single<BridgeEventContext> pContext) {
    
    if(!isEnable()){
      return pContext;
    }
    
    return pContext
            .flatMap((ctx) -> {
              if (ctx.getBridgeEvent().type() == BridgeEventType.REGISTER) {
                return mRedisClient
                        .rxIncr(ctx.getAddress())
                        .flatMap((i) -> {
                          return mRedisClient
                                  .rxExpire(ctx.getAddress(), getCounterExpire());
                        })
                        .map(count -> ctx);
              }

              if (ctx.getBridgeEvent().type() == BridgeEventType.UNREGISTER) {
                return mRedisClient
                        .rxDecr(ctx.getAddress())
                        .flatMap((i) -> {
                          return mRedisClient
                                  .rxExpire(ctx.getAddress(), getCounterExpire());
                        })
                        .map(count -> ctx);
              }

              return Single.just(ctx);
            });
  }

  public boolean isEnable() {
    return mEnable;
  }

  public void setEnable(boolean pEnable) {
    this.mEnable = pEnable;
  }

}
