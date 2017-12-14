package io.vertx.reactivex.redis;

import in.erail.glue.annotation.StartService;
import io.vertx.reactivex.core.Vertx;
import io.vertx.redis.RedisOptions;

/**
 *
 * @author vinay
 */
public class RedisClientInstance {

  private RedisClient mRedisClient;
  private Vertx mVertx;
  private boolean mEnable;
  private RedisOptions mRedisOptions;

  @StartService
  public void start() {

    if (isEnable()) {
      mRedisClient = RedisClient.create(getVertx(), getRedisOptions());
    }
  }

  public RedisClient getRedisClient() {
    return mRedisClient;
  }

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  public boolean isEnable() {
    return mEnable;
  }

  public void setEnable(boolean pEnable) {
    this.mEnable = pEnable;
  }

  public RedisOptions getRedisOptions() {
    return mRedisOptions;
  }

  public void setRedisOptions(RedisOptions pRedisOptions) {
    this.mRedisOptions = pRedisOptions;
  }

}
