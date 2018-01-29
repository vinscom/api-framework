package in.erail.service.leader;

import com.google.common.base.Strings;
import in.erail.service.SingletonServiceImpl;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.shareddata.AsyncMap;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventUpdate;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * <ul>
 * <li>
 * If there are N number of subscribers for Topic A. Then only one subscriber(Leader) out of N subscriber should be able to use send message feature to send message to N subscriber of Topic A.
 * </li>
 * <li>
 * One subscriber can hold leadership for limited time only. After that leadership must be transfered to another subscriber.
 * </li>
 * </ul>
 *
 * @author vinay
 */
public class LeaderSelectionService extends SingletonServiceImpl {

  public static final String DEFAULT_LEADER_STATUS = "IN_PROGRESS";
  private String mBridgeEventUpdateTopicName;
  private String mLeaderMapName;
  private CompletableFuture<AsyncMap<String, String>> mTopicLeaderMap;
  private long mNumberOfTryToSelectLeader = 3;
  private String mConfirmationMessageTopicFieldName = "leader";
  private long mLeaderConfirmationTimeout = 5000;
  private Pattern mAllowedAddressForLeaderRegex;
  private long mClusterMapKeyTimout = 8 * 60 * 1000;
  private String mSendMessageHeaderSessionFieldName = "session";

  public Single<LeaderContext> tryLeaderSelection(LeaderContext pCtx, String pDebugKey) {

    return Single
            .<LeaderContext>create((e) -> {
              if (!e.isDisposed()) {
                /**
                 * On each try, we want to listen on new topic. This to avoid getting confirmation from old leader Leader ID = Session + # + Salt
                 */
                pCtx.setConfirmationAddress(UUID.randomUUID().toString());
                e.onSuccess(pCtx);
              }
            })
            .flatMap((lc) -> {
              getLog().debug(() -> String.format("[%s] Sending confirmation message on [%s], expecting reply on [%s]", pDebugKey, lc.getAddress(), lc.getConfirmationAddress()));
              /**
               * {
               * "leader" : "Leader ID" }
               */
              JsonObject confirmationMsg = new JsonObject().put(getConfirmationMessageTopicFieldName(), lc.getConfirmationAddress());
              getVertx().eventBus().send(lc.getAddress(), confirmationMsg);
              return getVertx()
                      .eventBus()
                      .<JsonObject>consumer(lc.getConfirmationAddress())
                      .toObservable()
                      .firstOrError()
                      .timeout(getLeaderConfirmationTimeout(), TimeUnit.MILLISECONDS)
                      .map(msg -> {
                        String leaderSession = msg.headers().get(getSendMessageHeaderSessionFieldName());
                        if (Strings.isNullOrEmpty(leaderSession)) {
                          throw new RuntimeException(String.format("[%s] Header missing session", pDebugKey));
                        }
                        lc.setReplayAddress(msg.replyAddress());
                        lc.setLeaderSessionId(msg.headers().get(getSendMessageHeaderSessionFieldName()));
                        getLog().debug(() -> String.format("[%s] Setting setLeaderSessionId:[%s]", pDebugKey, lc.getLeaderSessionId()));
                        return lc;
                      })
                      .doOnSuccess((ctx) -> {
                        getLog().debug(() -> String.format("[%s] Got successful confirmation on Confirmation Address:[%s]. Sending final reply to client", pDebugKey, lc.getConfirmationAddress()));
                        getVertx().eventBus().send(ctx.getReplayAddress(), new JsonObject());
                      });
            })
            .retry(getNumberOfTryToSelectLeader());
  }

  protected void topicRegister(BridgeEventUpdate pEvent) {

    String debugKey = pEvent.getSession() + ":" + pEvent.getAddress() + ":" + pEvent.getType().toString();

    getLog().debug(() -> String.format("[%s] Processing %s", debugKey, pEvent.toString()));

    LeaderContext lctx = new LeaderContext();
    lctx.setAddress(pEvent.getAddress());

    AsyncMap<String, String> topicMap = getTopicLeaderMap();

    if (topicMap == null) {
      return;
    }

    topicMap
            .rxPutIfAbsent(lctx.getAddress(), DEFAULT_LEADER_STATUS, getClusterMapKeyTimout())
            .filter((key) -> key == null)
            .subscribe((key) -> {
              Single
                      .just(lctx)
                      .flatMap(lc -> tryLeaderSelection(lc, debugKey))
                      .flatMap((lc) -> topicMap.rxReplaceIfPresent(lc.getAddress(), DEFAULT_LEADER_STATUS, lc.getLeaderSessionId()))
                      .doOnSuccess((success) -> {
                        if (!success) {
                          getLog().error(String.format("[%s] Value for Address:[%s] has been updated", debugKey, lctx.getAddress()));
                        }
                      })
                      .onErrorReturn((err) -> {
                        getLog().error(String.format("[%s]", debugKey), err);
                        topicMap
                                .rxRemoveIfPresent(lctx.getAddress(), DEFAULT_LEADER_STATUS)
                                .subscribe((success) -> {
                                  if (!success) {
                                    getLog().error(String.format("[%s] Can't remove Key:[%s], value has been updated", debugKey, lctx.getAddress()));
                                  }
                                });
                        return false;
                      })
                      .subscribe();
            });
  }

  protected void topicUnregister(BridgeEventUpdate pEvent) {

    String debugKey = pEvent.getSession() + ":" + pEvent.getAddress() + ":" + pEvent.getType().toString();

    getLog().debug(() -> String.format("[%s] Processing %s", debugKey, pEvent.toString()));

    AsyncMap<String, String> topicMap = getTopicLeaderMap();

    if (topicMap == null) {
      return;
    }

    topicMap
            .rxRemoveIfPresent(pEvent.getAddress(), pEvent.getSession())
            .subscribe((success) -> {

              if (!success) {
                getLog().warn(() -> String.format("[%s] Not able to remove:[%s] from clustermap. Value has changed", debugKey, pEvent.getAddress()));
              }

              //Trigger selection of new leader
              BridgeEventUpdate beu = new BridgeEventUpdate();
              beu.setAddress(pEvent.getAddress());
              beu.setType(BridgeEventType.REGISTER);
              beu.setSession(UUID.randomUUID().toString());
              getVertx().eventBus().send(getBridgeEventUpdateTopicName(), beu.toJson());
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

  public AsyncMap<String, String> getTopicLeaderMap() {
    try {
      return mTopicLeaderMap.get();
    } catch (InterruptedException | ExecutionException ex) {
      getLog().error(ex);
    }
    return null;
  }

  public void setTopicLeaderMap(CompletableFuture<AsyncMap<String, String>> pTopicLeaderMap) {
    this.mTopicLeaderMap = pTopicLeaderMap;
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

  @Override
  public Completable startService() {

    mTopicLeaderMap = new CompletableFuture<>();

    getVertx()
            .sharedData()
            .<String, String>rxGetClusterWideMap(getLeaderMapName())
            .subscribeOn(Schedulers.computation()) //CompleteableFuture is blocking
            .subscribe((m) -> {
              mTopicLeaderMap.complete(m);
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

    return Completable.complete();
  }

}
