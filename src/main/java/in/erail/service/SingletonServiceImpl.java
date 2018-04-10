package in.erail.service;

import in.erail.glue.annotation.StartService;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.core.spi.cluster.NodeListener;
import io.vertx.reactivex.core.Vertx;
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

    getVertx()
            .sharedData()
            .<String, String>rxGetClusterWideMap(getServiceMapName())
            .subscribeOn(Schedulers.io())
            .flatMap((m) -> m.rxPutIfAbsent(getServiceName(), getClusterManager().getNodeID()))
            .map((ownerNodeId) -> {
              if (ownerNodeId == null) {
                return true;
              }
              return getClusterManager().getNodeID().equals(ownerNodeId);
            })
            .flatMapCompletable((success) -> {
              if (success) {
                getLog().info(String.format("Starting Service:[%s]", getServiceName()));
                return startService()
                        .doOnComplete(() -> getLog().info(String.format("Service:[%s] started", getServiceName())));
              }
              return Completable.complete();
            })
            .blockingAwait();
  }

  @Override
  public void nodeAdded(String pNodeID) {
  }

  @Override
  public void nodeLeft(String pNodeID) {

    if (!isEnable()) {
      return;
    }

    getVertx()
            .sharedData()
            .<String, String>rxGetClusterWideMap(getServiceMapName())
            .subscribeOn(Schedulers.io())
            .flatMap((m) -> m.rxReplaceIfPresent(getServiceName(), pNodeID, getClusterManager().getNodeID()))
            .flatMapCompletable((success) -> {
              if (success) {
                getLog().info(String.format("Starting Service:[%s] becuase of cluster state update", getServiceName()));
                return startService()
                        .doOnComplete(() -> getLog().info(String.format("Service:[%s] start complete because of cluster state update", getServiceName())));
              }
              return Completable.complete();
            })
            .blockingAwait();
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
