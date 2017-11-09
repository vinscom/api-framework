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
      setSessionStore(ClusteredSessionStore.create(getVertx()));
    } else {
      setSessionStore(LocalSessionStore.create(getVertx()));
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

  public SessionStore getSessionStore() {
    return mSessionStore;
  }

  public void setSessionStore(SessionStore pSessionStore) {
    this.mSessionStore = pSessionStore;
  }

}
