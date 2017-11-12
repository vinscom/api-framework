package io.vertx.core;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;

import java.util.Arrays;
import in.erail.glue.Glue;
import in.erail.glue.annotation.StartService;

public class VerticalDeployer {

  private String[] mVerticals;
  private DeploymentOptions mDeploymentOptions;
  private Vertx mVertx;

  @StartService
  public void start() {

    Arrays
            .stream(getVerticals())
            .forEach((path) -> {
              AbstractVerticle av = Glue.instance().<AbstractVerticle>resolve(path);
              getVertx().getDelegate().deployVerticle(av, getDeploymentOptions());
            });
  }

  public String[] getVerticals() {
    if (mVerticals == null) {
      return new String[]{};
    }
    return mVerticals;
  }

  public void setVerticals(String[] pVerticals) {
    mVerticals = pVerticals;
  }

  public DeploymentOptions getDeploymentOptions() {
    return mDeploymentOptions;
  }

  public void setDeploymentOptions(DeploymentOptions pDeploymentOptions) {
    this.mDeploymentOptions = pDeploymentOptions;
  }

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

}
