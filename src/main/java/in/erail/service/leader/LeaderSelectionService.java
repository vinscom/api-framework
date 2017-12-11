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
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class LeaderSelectionService {

  private Vertx mVertx;
  private Logger mLog;
  private String mBridgeEventUpdateTopicName;
  private String mLeaderMapName;
  private CompletableFuture<AsyncMap<String, String>> mTopicLeaderMap;
  private boolean mEnable = false;
  private long mLockAquireTimeout = 1000;
  private long mNumberOfTryToSelectLeader = 3;
  private String mConfirmationMessageTopicFieldName = "leader";
  private long mLeaderConfirmationTimeout = 5000;
  private Pattern mAllowedAddressForLeaderRegex;
  private long mClusterMapKeyTimout = 8 * 60 * 1000;
  private String mSendMessageHeaderSessionFieldName = "session";

  @StartService
  public void start() {

    if (isEnable()) {

      getLog().debug(() -> "Starting LeaderSelectionService");

      mTopicLeaderMap = new CompletableFuture<>();

      getVertx()
              .sharedData()
              .<String, String>rxGetClusterWideMap(getLeaderMapName())
              .subscribeOn(Schedulers.computation()) //CompleteableFuture is blocking
              .subscribe((m) -> {
                getTopicLeaderMap().complete(m);
                getLog().debug(() -> "Cluster Map reference aquired");
              });

      ConnectableFlowable<BridgeEventUpdate> events = getVertx()
              .eventBus()
              .<JsonObject>consumer(getBridgeEventUpdateTopicName())
              .toFlowable()
              .map(this::parse)
              .filter((e) -> e != null)
              .filter((e) -> getAllowedAddressForLeaderRegex().matcher(e.getAddress()).find())
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

    String debugKey = pEvent.getSession() + ":" + pEvent.getAddress() + ":" + pEvent.getType().toString();

    getLog().debug(() -> String.format("[%s] Processing %s", debugKey, pEvent.toString()));

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
                          getLog().debug(() -> String.format("[%s] Leader:[%s] found for [%s]", debugKey, v, lc.getAddress()));
                          lc.setError(true);
                        } else {
                          getLog().debug(() -> String.format("[%s] No leader set for [%s]", debugKey, lc.getAddress()));
                        }
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapSingle((lc) -> { //No leader found, acquire lock on address name
              return getVertx()
                      .sharedData()
                      .rxGetLockWithTimeout(lc.getAddress(), getLockAquireTimeout())
                      .map((l) -> {
                        getLog().debug(() -> String.format("[%s] Lock aquired on [%s]", debugKey, lc.getAddress()));
                        return lc.setLock(l);
                      })
                      .onErrorReturn((err) -> {
                        getLog().debug(() -> String.format("[%s] Could not aquire lock on [%s][%s]", debugKey, lc.getAddress(), err.toString()));
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
                          getLog().debug(() -> String.format("[%s] Found Leader[%s] after aquring lock on [%s]", debugKey, v, lc.getAddress()));
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
                          /**
                           * On each try, we want to listen on new topic. This to avoid getting confirmation from old leader Leader ID = Session + # + Salt
                           */
                          e.onSuccess(UUID.randomUUID().toString());
                        }
                      })
                      .flatMap((confirmationTopic) -> {
                        getLog().debug(() -> String.format("[%s] Sending confirmation message on [%s], expecting reply on [%s]", debugKey, lc.getAddress(), confirmationTopic));
                        /**
                         * {
                         * "leader" : "Leader ID" }
                         */
                        JsonObject confirmationMsg = new JsonObject().put(getConfirmationMessageTopicFieldName(), confirmationTopic);
                        getVertx().eventBus().send(lc.getAddress(), confirmationMsg);
                        return getVertx()
                                .eventBus()
                                .<JsonObject>consumer(confirmationTopic)
                                .toObservable()
                                .firstOrError()
                                .timeout(getLeaderConfirmationTimeout(), TimeUnit.MILLISECONDS)
                                .doOnSuccess((msg) -> {
                                  String leaderSession = msg.headers().get(getSendMessageHeaderSessionFieldName());
                                  getLog().debug(() -> String.format("[%s] Got confirmation for [%s] on [%s]", debugKey, lc.getAddress(), leaderSession));
                                  lc.setLeaderId(leaderSession); //Socket id of socket connected to leader
                                  msg.reply(new JsonObject());  //Send confirmation to client. Only after this confirmation, client becomes leader
                                });
                      })
                      .retry(getNumberOfTryToSelectLeader())
                      .doOnError((err) -> {
                        getLog().debug(() -> String.format("[%s] Error in sending message. Tried [%d] times", debugKey, getNumberOfTryToSelectLeader()));
                        lc.setError(true);
                      })
                      .onErrorReturn((err) -> {
                        getLog().debug(() -> String.format("[%s] No confirmation recieved", debugKey));
                        return null;
                      })
                      .map((msg) -> {
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapCompletable((lc) -> { //Store leader in session and map
              getLog().debug(() -> String.format("[%s] Updated Cluster Map Key:[%s],Value:[%s]", debugKey, lc.getAddress(), lc.getLeaderId()));
              return getTopicLeaderMap()
                      .get()
                      .rxPut(lc.getAddress(), lc.getLeaderId(), getClusterMapKeyTimout());
            })
            .doFinally(() -> {
              if (lctx.getLock() != null) {
                lctx.getLock().release();
                getLog().debug(() -> String.format("[%s] Releasing lock on [%s]", debugKey, lctx.getLock().toString()));
              } else {
                getLog().debug(() -> String.format("[%s] No lock aquired, so, nothing to release", debugKey));
              }
            })
            .onErrorComplete()
            .subscribe(() -> {
              getLog().debug(() -> String.format("[%s] Finished Processing [%s]", debugKey, pEvent.toString()));
            });

  }

  protected void topicUnregister(BridgeEventUpdate pEvent) {

    String debugKey = pEvent.getSession() + ":" + pEvent.getAddress() + ":" + pEvent.getType().toString();

    getLog().debug(() -> String.format("[%s] Processing %s", debugKey, pEvent.toString()));

    LeaderContext lctx = new LeaderContext();
    lctx.setAddress(pEvent.getAddress());
    lctx.setLeaderId(pEvent.getSession());

    Single
            .just(lctx)
            .flatMap((lc) -> {
              return getTopicLeaderMap()
                      .get()
                      .rxGet(lc.getAddress())
                      .map((v) -> {
                        if (!lc.getLeaderId().equals(v)) {
                          getLog().debug(() -> String.format("[%s] [%s] is not a leader. Stop Processing", debugKey, lc.getLeaderId()));
                          lc.setError(true);
                        } else {
                          getLog().debug(() -> String.format("[%s] [%s] is a leader", debugKey, lc.getLeaderId()));
                        }
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapSingle((lc) -> {
              return getVertx()
                      .sharedData()
                      .rxGetLockWithTimeout(lc.getAddress(), 1000)
                      .map((l) -> {
                        getLog().debug(() -> String.format("[%s] Lock aquired on [%s]", debugKey, lc.getAddress()));
                        return lc.setLock(l);
                      })
                      .onErrorReturn((err) -> {
                        getLog().debug(() -> String.format("[%s] Could not aquire lock on [%s][%s]", debugKey, lc.getAddress(), err.toString()));
                        lc.setError(true);
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapSingle((lc) -> {
              return getTopicLeaderMap()
                      .get()
                      .rxGet(lc.getAddress())
                      .map((v) -> {
                        if (!lc.getLeaderId().equals(v)) {
                          getLog().debug(() -> String.format("[%s] Found different Leader[%s] after aquring lock on [%s]", debugKey, v, lc.getAddress()));
                          lc.setError(true);
                        }
                        return lc;
                      });
            })
            .filter((lc) -> !lc.isError())
            .flatMapSingle((lc) -> {
              getLog().debug(() -> String.format("[%s] Removed ", debugKey, lc.getAddress()));
              return getTopicLeaderMap()
                      .get()
                      .rxRemove(lc.getAddress())
                      .map((v) -> lc);
            })
            .doOnSuccess((lc) -> {
              //Trigger selection of new leader
              BridgeEventUpdate beu = new BridgeEventUpdate();
              beu.setAddress(lc.getAddress());
              beu.setType(BridgeEventType.REGISTER);
              beu.setSession(UUID.randomUUID().toString());
              getVertx().eventBus().send(getBridgeEventUpdateTopicName(), beu.toJson());
            })
            .doFinally(() -> {
              if (lctx.getLock() != null) {
                lctx.getLock().release();
                getLog().debug(() -> String.format("[%s] Releasing lock on [%s]", debugKey, lctx.getLock().toString()));
              } else {
                getLog().debug(() -> String.format("[%s] No lock aquired, so, nothing to release", debugKey));
              }
            })
            .subscribe((t) -> {
              getLog().debug(() -> String.format("[%s] Finished Processing [%s]", debugKey, pEvent.toString()));
            });

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

  public long getLockAquireTimeout() {
    return mLockAquireTimeout;
  }

  public void setLockAquireTimeout(long pLockAquireTimeout) {
    this.mLockAquireTimeout = pLockAquireTimeout;
  }

  public long getNumberOfTryToSelectLeader() {
    return mNumberOfTryToSelectLeader;
  }

  public void setNumberOfTryToSelectLeader(long pNumberOfTryToSelectLeader) {
    this.mNumberOfTryToSelectLeader = pNumberOfTryToSelectLeader;
  }

  public String getConfirmationMessageTopicFieldName() {
    return mConfirmationMessageTopicFieldName;
  }

  public void setConfirmationMessageTopicFieldName(String pConfirmationMessageTopicFieldName) {
    this.mConfirmationMessageTopicFieldName = pConfirmationMessageTopicFieldName;
  }

  public long getLeaderConfirmationTimeout() {
    return mLeaderConfirmationTimeout;
  }

  public void setLeaderConfirmationTimeout(long pLeaderConfirmationTimeout) {
    this.mLeaderConfirmationTimeout = pLeaderConfirmationTimeout;
  }

  public Pattern getAllowedAddressForLeaderRegex() {
    return mAllowedAddressForLeaderRegex;
  }

  public void setAllowedAddressForLeaderRegex(Pattern pAllowedAddressForLeaderRegex) {
    this.mAllowedAddressForLeaderRegex = pAllowedAddressForLeaderRegex;
  }

  public long getClusterMapKeyTimout() {
    return mClusterMapKeyTimout;
  }

  public void setClusterMapKeyTimout(long pClusterMapKeyTimout) {
    this.mClusterMapKeyTimout = pClusterMapKeyTimout;
  }

  public String getSendMessageHeaderSessionFieldName() {
    return mSendMessageHeaderSessionFieldName;
  }

  public void setSendMessageHeaderSessionFieldName(String pSendMessageHeaderSessionFieldName) {
    this.mSendMessageHeaderSessionFieldName = pSendMessageHeaderSessionFieldName;
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
