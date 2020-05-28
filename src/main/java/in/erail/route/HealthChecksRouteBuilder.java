package in.erail.route;

import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.healthchecks.HealthChecks;
import io.vertx.reactivex.ext.web.Router;

/**
 *
 * @author vinay
 */
public class HealthChecksRouteBuilder extends AbstractRouterBuilderImpl {

  private HealthChecks mHealthChecks;

  /**
   *
   * @param pRouter
   * @return
   */
  @Override
  public Router getRouter(Router pRouter) {
    HealthCheckHandler hch = HealthCheckHandler.createWithHealthChecks(getHealthChecks());
    pRouter.route().handler(hch);
    return pRouter;
  }

  public HealthChecks getHealthChecks() {
    return mHealthChecks;
  }

  public void setHealthChecks(HealthChecks pHealthChecks) {
    this.mHealthChecks = pHealthChecks;
  }

}
