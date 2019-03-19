package in.erail.route;

import in.erail.common.FrameworkConstants;
import io.vertx.reactivex.ext.auth.User;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.Session;

/**
 *
 * @author vinay
 */
public class LoadUserFromSessionRouteBuillder extends AbstractRouterBuilderImpl {

  @Override
  public Router getRouter(Router pRouter) {
    pRouter.route().handler(this::handle);
    return pRouter;
  }

  public void handle(RoutingContext pRoutingContext) {
    Session session = pRoutingContext.session();

    if (session != null) {
      User principal = session.get(FrameworkConstants.Session.PRINCIPAL);
      if (principal != null) {
        pRoutingContext.setUser(principal);
      } else {
        getLog().debug("User not found");
        pRoutingContext.fail(401);
      }
    }
    pRoutingContext.next();
  }

}
