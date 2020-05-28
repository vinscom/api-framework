package in.erail.healthchecks;

import in.erail.glue.annotation.StartService;
import in.erail.glue.component.ServiceMap;
import io.vertx.core.Handler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.healthchecks.HealthChecks;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class HealthChecksRegistry {

  private HealthChecks mHealthChecks;
  private ServiceMap<Handler<Promise<Status>>> mChecks;
  private Vertx mVertx;
  private Logger mLog;

  @StartService
  public void start() {
    
    if (mHealthChecks == null) {
      mHealthChecks = HealthChecks.create(getVertx());
    }
    
    getChecks()
            .getServices()
            .forEach((t, u) -> {
              getHealthChecks().register(t, u);
              getLog().info("Registed HealthCheck:" + t);
            });
  }

  public HealthChecks getHealthChecks() {
    return mHealthChecks;
  }

  public void setHealthChecks(HealthChecks pHealthChecks) {
    this.mHealthChecks = pHealthChecks;
  }

  public ServiceMap<Handler<Promise<Status>>> getChecks() {
    return mChecks;
  }

  public void setChecks(ServiceMap<Handler<Promise<Status>>> pChecks) {
    this.mChecks = pChecks;
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

}
