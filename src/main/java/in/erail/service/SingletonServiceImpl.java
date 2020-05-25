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
    
    if(getVertx().isClustered()){
      getClusterManager().nodeListener(this);
    }
    
    init(Optional.empty())
            .subscribe(() -> {
            }, err -> getLog().error(err));
  }

  protected Completable init(Optional<String> pCurrentOwnerNodeId) {

    if (!isEnable()) {
      return Completable.complete();
    }

    return claimOwnership(pCurrentOwnerNodeId)
            .filter(t -> t)
            .flatMapCompletable(t -> startService())
            .doOnComplete(() -> getLog().info("Service Started:" + getServiceName()));
  }

  protected Single<Boolean> claimOwnership(Optional<String> pCurrentOwnerNodeId) {

    if (!getVertx().isClustered()) {
      return Single.just(true);
    }

    final String thisNodeId = getClusterManager().getNodeID();

    return getVertx()
            .sharedData()
            .rxGetClusterWideMap(getServiceMapName())
            .flatMap((m) -> {
              if (pCurrentOwnerNodeId.isPresent()) {
                return m.rxReplaceIfPresent(getServiceName(), pCurrentOwnerNodeId.get(), thisNodeId);
              }
              return m
                      .rxPutIfAbsent(getServiceName(), thisNodeId)
                      .map(t -> false)
                      .switchIfEmpty(Single.just(true));
            });

  }

  @Override
  public void nodeAdded(String pNodeID) {

  }

  @Override
  public void nodeLeft(String pNodeID) {
    Single
            .just(Optional.of(pNodeID))
            .flatMapCompletable(this::init)
            .subscribe(() -> {
            }, err -> getLog().error(err));
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
