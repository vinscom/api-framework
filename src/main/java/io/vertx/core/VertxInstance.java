package io.vertx.core;

import io.vertx.reactivex.core.Vertx;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import in.erail.glue.annotation.StartService;

public class VertxInstance {

  private VertxOptions mVertxOptions;
  private boolean mClusterEnable = true;
  private CompletableFuture<Vertx> mVertx = new CompletableFuture<>();

  @StartService
  public void start() {
    if (isClusterEnable()) {
      Vertx.rxClusteredVertx(getVertxOptions()).subscribe((t) -> mVertx.complete(t));
    } else {
      mVertx.complete(Vertx.vertx(getVertxOptions()));
    }

  }

  public Vertx create() throws InterruptedException, ExecutionException {
      return mVertx.get();
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
