package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import com.google.common.base.Strings;
import io.reactivex.Single;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventProcessor;
import io.vertx.reactivex.redis.RedisClient;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class SubscriberCountProcessor implements BridgeEventProcessor {

  private RedisClient mRedisClient;
  private long mCounterExpire;
  private boolean mEnable;
  private String mKeyPrefix;
  private Logger mLog;

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

    if (!isEnable()) {
      return pContext;
    }

    return pContext
            .flatMap((ctx) -> {
              
              if(Strings.isNullOrEmpty(ctx.getAddress())){
                getLog().error(() -> "Address can't empty");
                return Single.just(ctx);
              }
              
              if (ctx.getBridgeEvent().type() == BridgeEventType.REGISTER) {
                return mRedisClient
                        .rxIncr(getKeyPrefix() + ctx.getAddress())
                        .flatMap((i) -> {
                          return mRedisClient
                                  .rxExpire(getKeyPrefix() + ctx.getAddress(), getCounterExpire());
                        })
                        .map(count -> ctx);
              }

              if (ctx.getBridgeEvent().type() == BridgeEventType.UNREGISTER) {
                return mRedisClient
                        .rxDecr(getKeyPrefix() + ctx.getAddress())
                        .flatMap((i) -> {
                          return mRedisClient
                                  .rxExpire(getKeyPrefix() + ctx.getAddress(), getCounterExpire());
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

  public String getKeyPrefix() {
    return mKeyPrefix;
  }

  public void setKeyPrefix(String pKeyPrefix) {
    this.mKeyPrefix = pKeyPrefix;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

}
