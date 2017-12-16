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

              if (Strings.isNullOrEmpty(ctx.getAddress())) {
                getLog().error(() -> String.format("[%s] Address can't empty", ctx.getId() != null ? ctx.getId() : ""));
                return Single.just(ctx);
              }

              if (ctx.getBridgeEvent().type() == BridgeEventType.REGISTER) {
                getLog().debug(() -> String.format("[%s] Processing [%s] Type", ctx.getId(), ctx.getBridgeEvent().type().toString()));
                return mRedisClient
                        .rxIncr(ctx.getAddressKey())
                        .flatMap((i) -> {
                          getLog().debug(() -> String.format("[%s] Incremented Key:[%s],Value:[%d]", ctx.getId(), ctx.getAddressKey(), i));
                          return mRedisClient
                                  .rxExpire(ctx.getAddressKey(), getCounterExpire());
                        })
                        .doOnSuccess((t) -> {
                          getLog().debug(() -> String.format("[%s] Expiry Set Key:[%s],Time:[%d] during REGISTER", ctx.getId(), ctx.getAddressKey(), getCounterExpire()));
                        })
                        .map(count -> ctx);
              }

              if (ctx.getBridgeEvent().type() == BridgeEventType.UNREGISTER) {
                getLog().debug(() -> String.format("[%s] Processing [%s] Type", ctx.getId(), ctx.getBridgeEvent().type().toString()));
                return mRedisClient
                        .rxDecr(ctx.getAddressKey())
                        .flatMap((i) -> {
                          getLog().debug(() -> String.format("[%s] Decremented Key:[%s],Value:[%d]", ctx.getId(), ctx.getAddressKey(), i));
                          return mRedisClient
                                  .rxExpire(ctx.getAddressKey(), getCounterExpire());
                        })
                        .doOnSuccess((t) -> {
                          getLog().debug(() -> String.format("[%s] Expiry Set Key:[%s],Time:[%d] during UNREGISTER", ctx.getId(), ctx.getAddressKey(), getCounterExpire()));
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
