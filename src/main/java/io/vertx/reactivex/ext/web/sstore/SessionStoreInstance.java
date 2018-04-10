package io.vertx.reactivex.ext.web.sstore;

import io.vertx.reactivex.core.Vertx;
import in.erail.glue.annotation.StartService;

/**
 *
 * @author vinay
 */
public class SessionStoreInstance {

  private Vertx mVertx;
  private boolean mClusterEnable;
  private SessionStore mSessionStore;

  @StartService
  public void start() {
    if (isClusterEnable()) {
      mSessionStore = ClusteredSessionStore.create(getVertx());
    } else {
      mSessionStore = LocalSessionStore.create(getVertx());
    }
  }

  public boolean isClusterEnable() {
    return mClusterEnable;
  }

  public void setClusterEnable(boolean pClusterEnable) {
    this.mClusterEnable = pClusterEnable;
  }

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  public SessionStore create() {
    return mSessionStore;
  }

}
