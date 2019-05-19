package io.vertx.core;

import io.vertx.reactivex.core.Vertx;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import in.erail.glue.annotation.StartService;
import io.reactivex.plugins.RxJavaPlugins;
import io.vertx.reactivex.core.RxHelper;

public class VertxInstance {

  private VertxOptions mVertxOptions;
  private boolean mClusterEnable = true;
  private final CompletableFuture<Vertx> mVertx = new CompletableFuture<>();

  @StartService
  public void start() {
    if (isClusterEnable()) {
      Vertx.rxClusteredVertx(getVertxOptions()).subscribe((t) -> mVertx.complete(t));
    } else {
      mVertx.complete(Vertx.vertx(getVertxOptions()));
    }

  }

  public Vertx create() throws InterruptedException, ExecutionException {
    Vertx v = mVertx.get();
    RxJavaPlugins.setComputationSchedulerHandler(s -> RxHelper.scheduler(v));
    RxJavaPlugins.setIoSchedulerHandler(s -> RxHelper.blockingScheduler(v));
    RxJavaPlugins.setNewThreadSchedulerHandler(s -> RxHelper.scheduler(v));
    return v;
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
