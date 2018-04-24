package io.vertx.core;

import io.vertx.reactivex.core.Vertx;

import java.util.Arrays;
import in.erail.glue.annotation.StartService;
import io.vertx.core.json.JsonObject;
import java.util.Optional;
import org.apache.logging.log4j.Logger;

public class VerticalDeployer {

  private String[] mVerticalNames;
  private Verticle[] mVerticalInstances;
  private DeploymentOptions mDeploymentOptions;
  private Vertx mVertx;
  private Logger mLog;
  private String mDeployStatusTopicName;

  @StartService
  public void start() {

    //Deploy using name
    Optional
            .ofNullable(getVerticalNames())
            .ifPresent(t -> {
              Arrays
                      .stream(getVerticalNames())
                      .forEach((path) -> {
                        JsonObject deployMsg = new JsonObject();
                        deployMsg.put("name", path);
                        getVertx()
                                .getDelegate()
                                .deployVerticle(path, getDeploymentOptions(), (result) -> {
                                  if (result.succeeded()) {
                                    String deploymentId = result.result();
                                    deployMsg.put("deploymentId", deploymentId);
                                    deployMsg.put("success", true);
                                    getVertx().eventBus().publish(getDeployStatusTopicName(), deployMsg);
                                    getLog().info(() -> "Deployed:" + path + ":" + deploymentId);
                                  } else {
                                    deployMsg.put("success", false);
                                    getVertx().eventBus().publish(getDeployStatusTopicName(), deployMsg);
                                    getLog().error("Vertical Deployment failed", result.cause());
                                  }
                                });
                      });
            });

    //Deploy using Java class instance
    Optional
            .ofNullable(getVerticalInstances())
            .ifPresent(t -> {
              Arrays
                      .stream(t)
                      .forEach((vertical) -> {

                        JsonObject deployMsg = new JsonObject();
                        deployMsg.put("name", vertical.getClass().getCanonicalName());
                        getVertx()
                                .getDelegate()
                                .deployVerticle(vertical, getDeploymentOptions(), (result) -> {
                                  if (result.succeeded()) {
                                    String deploymentId = result.result();
                                    deployMsg.put("deploymentId", deploymentId);
                                    deployMsg.put("success", true);
                                    getVertx().eventBus().publish(getDeployStatusTopicName(), deployMsg);
                                    getLog().info(() -> "Deployed:" + vertical.getClass().getCanonicalName() + ":" + deploymentId);
                                  } else {
                                    deployMsg.put("success", false);
                                    getVertx().eventBus().publish(getDeployStatusTopicName(), deployMsg);
                                    getLog().error("Vertical Deployment failed", result.cause());
                                  }
                                });
                      });

            });

  }

  public String[] getVerticalNames() {
    return mVerticalNames;
  }

  public void setVerticalNames(String[] pVerticalNames) {
    mVerticalNames = pVerticalNames;
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

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public String getDeployStatusTopicName() {
    return mDeployStatusTopicName;
  }

  public void setDeployStatusTopicName(String pDeployStatusTopicName) {
    this.mDeployStatusTopicName = pDeployStatusTopicName;
  }

  public Verticle[] getVerticalInstances() {
    return mVerticalInstances;
  }

  public void setVerticalInstances(Verticle[] pVerticalInstances) {
    this.mVerticalInstances = pVerticalInstances;
  }

}
