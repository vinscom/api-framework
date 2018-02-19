package io.vertx.core;

import io.vertx.reactivex.core.Vertx;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import in.erail.glue.annotation.StartService;

public class VertxInstance {

  private VertxOptions mVertxOptions;
  private boolean mClusterEnable = true;
  private CompletableFuture<Vertx> mVertx = null;

  @StartService
  public void start() {

    if (isClusterEnable()) {
      mVertx = new CompletableFuture<>();
      Vertx.rxClusteredVertx(getVertxOptions()).subscribe((t) -> mVertx.complete(t));
    } else {
      mVertx = CompletableFuture.completedFuture(Vertx.vertx(getVertxOptions()));
    }

  }

  public Vertx createVertx() {

    try {
      return mVertx.get();
    } catch (InterruptedException | ExecutionException ex) {
      Logger.getLogger(VertxInstance.class.getName()).log(Level.SEVERE, null, ex);
    }

    return null;
  }

  public VertxOptions getVertxOptions() {
    return mVertxOptions;
  }

  public void setVertxOptions(VertxOptions pVertxOptions) {
    this.mVertxOptions = pVertxOptions;
  }

  public boolean isClusterEnable() {
    return mClusterEnable;
  }

  public void setClusterEnable(boolean pClusterEnable) {
    this.mClusterEnable = pClusterEnable;
  }

}
