package in.erail.service;

import in.erail.glue.annotation.StartService;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.core.spi.cluster.NodeListener;
import io.vertx.reactivex.core.Vertx;
import java.util.Optional;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public abstract class SingletonServiceImpl implements NodeListener, SingletonService {

  private Vertx mVertx;
  private String mServiceMapName = "__in.erail.services";
  private String mServiceName;
  private ClusterManager mClusterManager;
  private Logger mLog;
  private boolean mEnable;

  @StartService
  public void start() {

    if (!isEnable()) {
      return;
    }

    Single
            .just(Optional.<String>empty())
            .flatMapCompletable(this::init)
            .subscribe();
  }

  protected Completable init(Optional<String> pOldNodeID) {
    final String serviceName = getServiceName();
    final String thisNodeId = getClusterManager().getNodeID();

    return Single
            .just(getServiceMapName())
            .flatMap(name -> getVertx().sharedData().<String, String>rxGetClusterWideMap(name))
            .flatMap(m -> {
              if (pOldNodeID.isPresent()) {
                return m
                        .rxReplaceIfPresent(getServiceName(), pOldNodeID.get(), thisNodeId)
                        .map(v -> v ? thisNodeId : "");
              }
              return m
                      .rxPutIfAbsent(serviceName, thisNodeId)
                      .switchIfEmpty(Single.just(thisNodeId));
            })
            .doOnSuccess(serviceOwnerId -> getLog().debug(() -> "Service Owner ID:" + serviceOwnerId + ", This Node ID:" + thisNodeId))
            .map(serviceOwnerId -> thisNodeId.equals(serviceOwnerId))
            .doOnSuccess(t -> getLog().debug(() -> "Service Start Decision:" + getServiceName() + ":" + t))
            .flatMapCompletable((success) -> {
              if (success) {
                getLog().info(String.format("Starting Service:[%s]", getServiceName()));
                return startService()
                        .doOnComplete(() -> getLog().info(String.format("Service:[%s] started", getServiceName())));
              }
              return Completable.complete();
            });

  }

  @Override
  public void nodeAdded(String pNodeID) {
  }

  @Override
  public void nodeLeft(String pNodeID) {

    if (!isEnable()) {
      return;
    }

    Single
            .just(Optional.of(pNodeID))
            .flatMapCompletable(this::init)
            .subscribe();
  }

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  public String getServiceMapName() {
    return mServiceMapName;
  }

  public void setServiceMapName(String pServiceMapName) {
    this.mServiceMapName = pServiceMapName;
  }

  public String getServiceName() {
    return mServiceName;
  }

  public void setServiceName(String pServiceName) {
    this.mServiceName = pServiceName;
  }

  public ClusterManager getClusterManager() {
    return mClusterManager;
  }

  public void setClusterManager(ClusterManager pClusterManager) {
    this.mClusterManager = pClusterManager;
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
