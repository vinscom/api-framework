package io.vertx.reactivex.ext.mongo;

import in.erail.glue.annotation.StartService;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

/**
 *
 * @author vinay
 */
public class MongoClientInstance {

  private MongoClient mMongoClient;
  private Vertx mVertx;
  private JsonObject mConfig;
  private boolean mEnable;

  @StartService
  public void start() {
    if (isEnable()) {
      mMongoClient = MongoClient.createShared(getVertx(), getConfig());
    }
  }

  public MongoClient getMongoClient() {
    return mMongoClient;
  }

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  public JsonObject getConfig() {
    return mConfig;
  }

  public void setConfig(JsonObject pConfig) {
    this.mConfig = pConfig;
  }

  public boolean isEnable() {
    return mEnable;
  }

  public void setEnable(boolean pEnable) {
    this.mEnable = pEnable;
  }

}
