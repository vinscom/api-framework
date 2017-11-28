package in.erail.service.info;

import in.erail.common.FramworkConstants;

import in.erail.service.ServiceImpl;
import io.reactivex.Observable;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.shareddata.AsyncMap;
import java.util.Set;

/**
 *
 * @author vinay
 */
public class SubscriberReportService extends ServiceImpl {

  private String mClusterSubscriberMapKey;

  private Observable<String> createEntriesObservable(AsyncMap<String, Boolean> pMap) {
    return Observable.<String>create((emitter) -> {
      pMap.getDelegate().keys((keys) -> {
        Future<Set<String>> k = (Future<Set<String>>) keys;
        Observable
                .fromIterable(k.result())
                .doOnNext((t) -> emitter.onNext(t))
                .doOnComplete(() -> emitter.onComplete())
                .subscribe();
      });
    });
  }

  @Override
  public void process(Message<JsonObject> pMessage) {

    JsonArray result = new JsonArray();

    getVertx()
            .sharedData()
            .<String, Boolean>rxGetClusterWideMap(getClusterSubscriberMapKey())
            .flatMapObservable(this::createEntriesObservable)
            .flatMapSingle((key) -> {
              JsonObject item = new JsonObject().put("topic", key);
              return getVertx()
                      .sharedData()
                      .rxGetCounter(getClusterSubscriberMapKey() + key)
                      .flatMap((counter) -> counter.rxGet())
                      .map((count) -> item.put("count", count));
            })
            .doOnNext((item) -> result.add(item))
            .doOnComplete(() -> {
              JsonObject replyMsg = new JsonObject();
              replyMsg.put(FramworkConstants.RoutingContext.Json.BODY, result);
              pMessage.reply(replyMsg);
            })
            .subscribe();
  }

  public String getClusterSubscriberMapKey() {
    return mClusterSubscriberMapKey;
  }

  public void setClusterSubscriberMapKey(String pClusterSubscriberMapKey) {
    this.mClusterSubscriberMapKey = pClusterSubscriberMapKey;
  }

}
