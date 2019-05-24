package io.vertx.core;

import io.vertx.reactivex.core.Vertx;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import in.erail.glue.annotation.StartService;
import io.reactivex.plugins.RxJavaPlugins;
import io.vertx.reactivex.core.RxHelper;
import org.apache.logging.log4j.Logger;

public class VertxInstance {

  private Logger mLog;
  private VertxOptions mVertxOptions;
  private final CompletableFuture<Vertx> mVertx = new CompletableFuture<>();

  @StartService
  public void start() {
    if (getVertxOptions().getEventBusOptions().isClustered()) {
      getLog().info(() -> "Starting Vertx in Cluster Mode");
      Vertx
              .rxClusteredVertx(getVertxOptions())
              .doOnSuccess(v -> getLog().info(() -> "Vertx is running in cluster mode"))
              .subscribe((t) -> mVertx.complete(t));
    } else {
      getLog().info(() -> "Starting Vertx in Cluster Mode");
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

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

}
