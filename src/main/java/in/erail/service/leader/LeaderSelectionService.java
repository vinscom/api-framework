package in.erail.service.leader;

import com.google.common.base.Strings;
import in.erail.glue.annotation.StartService;
import io.reactivex.Single;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.shareddata.AsyncMap;
import io.vertx.reactivex.core.shareddata.Lock;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventUpdate;
import io.vertx.reactivex.ext.web.sstore.SessionStore;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class LeaderSelectionService {

  private Vertx mVertx;
  private Logger mLog;
  private String mBridgeEventUpdateTopicName;
  private SessionStore mSessionStore;
  private String mLeaderMapName;
  private CompletableFuture<AsyncMap<String, String>> mTopicLeaderMap;
  private boolean mEnable = false;

  @StartService
  public void start() {

    if (isEnable()) {

      mTopicLeaderMap = new CompletableFuture<>();

      getVertx()
              .sharedData()
              .<String, String>rxGetClusterWideMap(getLeaderMapName())
              .subscribeOn(Schedulers.computation()) //CompleteableFuture is blocking
              .subscribe((m) -> getTopicLeaderMap().complete(m));

      ConnectableFlowable<BridgeEventUpdate> events = getVertx()
              .eventBus()
              .<JsonObject>consumer(getBridgeEventUpdateTopicName())
              .toFlowable()
              .map(this::parse)
              .filter((e) -> e != null)
              .publish();

      events
              .filter(e -> BridgeEventType.REGISTER.equals(e.getType()))
              .subscribe(this::topicRegister);

      events
              .filter(e -> BridgeEventType.UNREGISTER.equals(e.getType()))
              .subscribe(this::topicUnregister);

      events.connect();
    }

  }

  protected void topicRegister(BridgeEventUpdate pEvent) {

    LeaderContext lctx = new LeaderContext();
    lctx.setAddress(pEvent.getAddress());

    Single
            .just(lctx)
            .flatMap((lc) -> {  //Check if topic has leader
              return getTopicLeaderMap()
                      .get()
                      .rxGet(lc.getAddress())
                      .map((v) -> {
                        if (!Strings.isNullOrEmpty(v)) {
                          getLog().debug("Key found, Leader already selected for topic:" + lc.getAddress());
                          lc.setError(true);
                        }
                        getLog().debug("No leader set for topic:" + lc.getAddress());
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapSingle((lc) -> { //No leader found, acquire lock on address name
              return getVertx()
                      .sharedData()
                      .rxGetLockWithTimeout(lc.getAddress(), 1000)
                      .map((l) -> lc.setLock(l))
                      .onErrorReturn((err) -> {
                        getLog().debug("Could not aquire lock for topic:" + lc.getAddress());
                        lc.setError(true);
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapSingle((lc) -> {  //Check again if leader is selected by the time we aquired lock
              return getTopicLeaderMap()
                      .get()
                      .rxGet(lc.getAddress())
                      .map((v) -> {
                        if (!Strings.isNullOrEmpty(v)) {
                          getLog().debug("Found leader after aquring lock on topic:" + lc.getAddress());
                          lc.setError(true);
                        }
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapSingle((lc) -> {  // Select leader by sending unique id and wait for confirmation.

              return Single
                      .<String>create((e) -> {
                        if (!e.isDisposed()) {
                          e.onSuccess(pEvent.getSession() + "#" + UUID.randomUUID().toString());
                        }
                      })
                      .flatMap((leaderId) -> {
                        getLog().debug("Sending Message for Confirmation leaderId:" + leaderId + ":" + lc.getAddress());
                        /**
                         * {
                         *  "leader" : "Leader ID"
                         * }
                         */
                        getVertx().eventBus().send(lc.getAddress(), new JsonObject().put("leader", leaderId));
                        return getVertx()
                                .eventBus()
                                .<JsonObject>consumer(leaderId)
                                .toObservable()
                                .firstOrError()
                                .timeout(5, TimeUnit.SECONDS)
                                .doOnSuccess((msg) -> {
                                  //TODO: Confirm message content and then set
                                  getLog().debug("Got confirmation from leader:" + leaderId + ":" + lc.getAddress());
                                  lc.setLeaderId(leaderId.split("#")[0]);
                                });
                      })
                      .retry(3)
                      .doOnError((err) -> {
                        getLog().debug("Error in sending message after 3 retry");
                        lc.setError(true);
                      })
                      .onErrorReturn((err) -> {
                        getLog().debug("No confirmation recieved");
                        return null;
                      })
                      .map((msg) -> {
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapCompletable((lc) -> { //Store leader in session and map
              return getTopicLeaderMap()
                      .get()
                      .rxPut(lc.getAddress(), lc.getLeaderId());
            })
            .doFinally(() -> {
              if (lctx.getLock() != null) {
                lctx.getLock().release();
                getLog().debug("Releasing lock:" + lctx.getLock().toString());
              } else {
                getLog().debug("Nothing to releasing lock");
              }
            })
            .onErrorComplete()
            .subscribe();

  }

  protected void topicUnregister(BridgeEventUpdate pEvent) {

    if (Strings.isNullOrEmpty(pEvent.getSession())) {
      return;
    }

    LeaderContext lctx = new LeaderContext();
    lctx.setAddress(pEvent.getAddress());
    lctx.setLeaderId(pEvent.getSession());

    Single
            .just(lctx)
            .flatMap((lc) -> {
              return getTopicLeaderMap()
                      .get()
                      .rxGet(lc.getAddress())
                      .map((t) -> {
                        if (!lc.getLeaderId().equals(t)) {
                          lc.setError(true);
                        }
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapSingle((lc) -> {
              return getVertx()
                      .sharedData()
                      .rxGetLockWithTimeout(lc.getAddress(), 1000)
                      .map((l) -> lc.setLock(l))
                      .onErrorReturn((err) -> {
                        getLog().error("Could not aquire lock for topic:" + lc.getAddress());
                        lc.setError(true);
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapSingle((lc) -> {
              return getTopicLeaderMap()
                      .get()
                      .rxGet(lc.getAddress())
                      .map((t) -> {
                        if (!lc.getLeaderId().equals(t)) {
                          lc.setError(true);
                        }
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapSingle((lc) -> {
              return getTopicLeaderMap()
                      .get()
                      .rxRemove(lc.getAddress());
            })
            .onErrorReturnItem("")
            .doFinally(() -> {
              if (lctx.getLock() != null) {
                lctx.getLock().release();
                getLog().debug("Releasing lock:" + lctx.getLock().toString());
              } else {
                getLog().debug("Nothing to releasing lock");
              }
            })
            .subscribe();

  }

  protected BridgeEventUpdate parse(Message<JsonObject> pMessage) {
    try {
      return BridgeEventUpdate.parse(pMessage.body());
    } catch (IOException ex) {
      getLog().error(ex);
    }
    return null;
  }

  protected boolean filterBridgeEvent(Message<JsonObject> pMessage) {
    return false;
  }

  public String getBridgeEventUpdateTopicName() {
    return mBridgeEventUpdateTopicName;
  }

  public void setBridgeEventUpdateTopicName(String pBridgeEventUpdateTopicName) {
    this.mBridgeEventUpdateTopicName = pBridgeEventUpdateTopicName;
  }

  public SessionStore getSessionStore() {
    return mSessionStore;
  }

  public void setSessionStore(SessionStore pSessionStore) {
    this.mSessionStore = pSessionStore;
  }

  public String getLeaderMapName() {
    return mLeaderMapName;
  }

  public void setLeaderMapName(String pLeaderMapName) {
    this.mLeaderMapName = pLeaderMapName;
  }

  public CompletableFuture<AsyncMap<String, String>> getTopicLeaderMap() {
    return mTopicLeaderMap;
  }

  public void setTopicLeaderMap(CompletableFuture<AsyncMap<String, String>> pTopicLeaderMap) {
    this.mTopicLeaderMap = pTopicLeaderMap;
  }

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public boolean isEnable() {
    return mEnable;
  }

  public void setEnable(boolean pEnable) {
    this.mEnable = pEnable;
  }

}

class LeaderContext {

  private Lock mLock;
  private String mAddress;
  private String mLeaderId;
  private boolean mError = false;

  public Lock getLock() {
    return mLock;
  }

  public LeaderContext setLock(Lock pLock) {
    this.mLock = pLock;
    return this;
  }

  public String getAddress() {
    return mAddress;
  }

  public LeaderContext setAddress(String pAddress) {
    this.mAddress = pAddress;
    return this;
  }

  public String getLeaderId() {
    return mLeaderId;
  }

  public LeaderContext setLeaderId(String pLeaderId) {
    this.mLeaderId = pLeaderId;
    return this;
  }

  public boolean isError() {
    return mError;
  }

  public void setError(boolean pError) {
    this.mError = pError;
  }

}
