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
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class RateLimiterProcessor implements BridgeEventProcessor {

  private final Map<BridgeEventType, Cache<String, Bucket>> mCache = new EnumMap<>(BridgeEventType.class);
  private final Map<BridgeEventType, Integer> mTokenBucketSize = new EnumMap<>(BridgeEventType.class);
  private final Map<BridgeEventType, Integer> mRateOfTokenFill = new EnumMap<>(BridgeEventType.class);
  private final Map<BridgeEventType, Integer> mRateOfTokenFillDuration = new EnumMap<>(BridgeEventType.class);
  private int mDefaultTokenBucketSize = 120;
  private int mDefaultRateOfTokenFill = 1;
  private int mDefaultRateOfTokenFillDuration = 1;
  private int mMaximumSize = 50000;
  private int mExpireAfterAccess = 3600;
  private boolean mEnable = true;
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
                Refill refill = Refill.smooth(getRateOfTokenFill().getOrDefault(eventType, getDefaultRateOfTokenFill()),
                         Duration.ofSeconds(getRateOfTokenFillDuration().getOrDefault(eventType, getDefaultRateOfTokenFillDuration())));
                Bandwidth limit = Bandwidth.classic(getTokenBucketSize().getOrDefault(eventType, getDefaultTokenBucketSize()), refill);
                return Bucket4j.builder().addLimit(limit).build();
              });

              if (!bucket.tryConsume(1)) {
                getLog().debug(() -> String.format("[%s] Rate limit crossed for connection:[%s],event:[%s]", ctx.getId(), ctx.getBridgeEvent().socket().writeHandlerID(), eventType.toString()));
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

  public Map<BridgeEventType, Integer> getTokenBucketSize() {
    return mTokenBucketSize;
  }

  public Map<BridgeEventType, Integer> getRateOfTokenFill() {
    return mRateOfTokenFill;
  }

  public Map<BridgeEventType, Integer> getRateOfTokenFillDuration() {
    return mRateOfTokenFillDuration;
  }

  public void setTokenBucketSize(Map<String, String> pTokenBucketSize) {
    for (BridgeEventType type : BridgeEventType.values()) {
      if (pTokenBucketSize.containsKey(type.toString())) {
        String value = pTokenBucketSize.get(type.toString());
        mTokenBucketSize.put(type, Integer.parseInt(value));
      } else {
        mTokenBucketSize.put(type, getDefaultTokenBucketSize());
      }
    }
  }

  public void setRateOfTokenFill(Map<String, String> pRateOfTokenFill) {
    for (BridgeEventType type : BridgeEventType.values()) {
      if (pRateOfTokenFill.containsKey(type.toString())) {
        String value = pRateOfTokenFill.get(type.toString());
        mRateOfTokenFill.put(type, Integer.parseInt(value));
      } else {
        mRateOfTokenFill.put(type, getDefaultRateOfTokenFill());
      }
    }
  }

  public void setRateOfTokenFillDuration(Map<String, String> pRateOfTokenFillDuration) {
    for (BridgeEventType type : BridgeEventType.values()) {
      if (pRateOfTokenFillDuration.containsKey(type.toString())) {
        String value = pRateOfTokenFillDuration.get(type.toString());
        mRateOfTokenFillDuration.put(type, Integer.parseInt(value));
      } else {
        mRateOfTokenFillDuration.put(type, getDefaultRateOfTokenFillDuration());
      }
    }
  }

  public int getDefaultTokenBucketSize() {
    return mDefaultTokenBucketSize;
  }

  public void setDefaultTokenBucketSize(int pDefaultTokenBucketSize) {
    this.mDefaultTokenBucketSize = pDefaultTokenBucketSize;
  }

  public int getDefaultRateOfTokenFill() {
    return mDefaultRateOfTokenFill;
  }

  public void setDefaultRateOfTokenFill(int pDefaultRateOfTokenFill) {
    this.mDefaultRateOfTokenFill = pDefaultRateOfTokenFill;
  }

  public int getDefaultRateOfTokenFillDuration() {
    return mDefaultRateOfTokenFillDuration;
  }

  public void setDefaultRateOfTokenFillDuration(int pDefaultRateOfTokenFillDuration) {
    this.mDefaultRateOfTokenFillDuration = pDefaultRateOfTokenFillDuration;
  }

}
