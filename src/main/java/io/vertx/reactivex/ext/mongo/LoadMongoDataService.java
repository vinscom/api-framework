package io.vertx.reactivex.ext.mongo;

import in.erail.glue.annotation.StartService;
import io.reactivex.Observable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Set;

/**
 *
 * @author vinay
 */
public class LoadMongoDataService {

  private MongoClient mMongoClient;
  private JsonObject mData;
  private boolean mEnable;

  @StartService
  public void start() {

    if (!isEnable()) {
      return;
    }

    Set<String> collections = mData.fieldNames();
    for (String collection : collections) {
      mMongoClient
              .rxDropCollection(collection)
              .andThen(mMongoClient.rxCreateCollection(collection))
              .blockingAwait();

      JsonArray data = mData.getJsonArray(collection);

      Observable
              .fromIterable(data)
              .map(m -> (JsonObject) m)
              .doOnNext((t) -> {
                mMongoClient.rxSave(collection, t).blockingGet();
              }).blockingSubscribe();
    }
  }

  public MongoClient getMongoClient() {
    return mMongoClient;
  }

  public void setMongoClient(MongoClient pMongoClient) {
    this.mMongoClient = pMongoClient;
  }

  public JsonObject getData() {
    return mData;
  }

  public void setData(JsonObject pData) {
    this.mData = pData;
  }

  public boolean isEnable() {
    return mEnable;
  }

  public void setEnable(boolean pEnable) {
    this.mEnable = pEnable;
  }
}
