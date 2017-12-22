package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import in.erail.glue.annotation.StartService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.reactivex.Single;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class RateLimiterProcessor implements BridgeEventProcessor {

  private Map<BridgeEventType, Cache<String, Bucket>> mCache = new HashMap<>(9);
  private int mMaximumSize = 50000;
  private int mExpireAfterAccess = 3600;
  private boolean mEnable = true;
  private int mTokenBucketSize = 120;
  private int mRateOfTokenFill = 1;
  private int mRateOfTokenFillDuration = 1;
  private Logger mLog;

  @StartService
  public void start() {
    for (BridgeEventType type : BridgeEventType.values()) {
      Cache<String, Bucket> cache = CacheBuilder
              .newBuilder()
              .recordStats()
              .expireAfterAccess(getExpireAfterAccess(), TimeUnit.SECONDS)
              .maximumSize(getMaximumSize())
              .build();
      getCache().put(type, cache);
    }
  }

  @Override
  public Single<BridgeEventContext> process(Single<BridgeEventContext> pContext) {

    return pContext
            .map((ctx) -> {

              if (ctx.getBridgeEvent().failed() || !isEnable()) {
                return ctx;
              }

              BridgeEventType eventType = ctx.getBridgeEvent().type();

              Bucket bucket = getCache().get(eventType).get(ctx.getBridgeEvent().socket().writeHandlerID(), () -> {
                Refill refill = Refill.smooth(getRateOfTokenFill(), Duration.ofSeconds(getRateOfTokenFillDuration()));
                Bandwidth limit = Bandwidth.classic(getTokenBucketSize(), refill);
                return Bucket4j.builder().addLimit(limit).build();
              });

              if (!bucket.tryConsume(1)) {
                getLog().debug(() -> String.format("[%s] Rate limit crossed for event:[%s]", ctx.getId(), eventType.toString()));
                ctx.getBridgeEvent().fail("Rate Limit Crossed");
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

  public int getMaximumSize() {
    return mMaximumSize;
  }

  public void setMaximumSize(int pMaximumSize) {
    this.mMaximumSize = pMaximumSize;
  }

  public int getExpireAfterAccess() {
    return mExpireAfterAccess;
  }

  public void setExpireAfterAccess(int pExpireAfterAccess) {
    this.mExpireAfterAccess = pExpireAfterAccess;
  }

  public boolean isEnable() {
    return mEnable;
  }

  public void setEnable(boolean pEnable) {
    this.mEnable = pEnable;
  }

  public Map<BridgeEventType, Cache<String, Bucket>> getCache() {
    return mCache;
  }

  public void setCache(Map<BridgeEventType, Cache<String, Bucket>> pCache) {
    this.mCache = pCache;
  }

  public int getTokenBucketSize() {
    return mTokenBucketSize;
  }

  public void setTokenBucketSize(int pTokenBucketSize) {
    this.mTokenBucketSize = pTokenBucketSize;
  }

  public int getRateOfTokenFill() {
    return mRateOfTokenFill;
  }

  public void setRateOfTokenFill(int pRateOfTokenFill) {
    this.mRateOfTokenFill = pRateOfTokenFill;
  }

  public int getRateOfTokenFillDuration() {
    return mRateOfTokenFillDuration;
  }

  public void setRateOfTokenFillDuration(int pRateOfTokenFillDuration) {
    this.mRateOfTokenFillDuration = pRateOfTokenFillDuration;
  }

}
