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
      io.vertx.ext.auth.User user = session.get(FrameworkConstants.Session.PRINCIPAL);

      if (user == null) {
        getLog().debug("User not found");
      } else {
        pRoutingContext.setUser(new User(user));
      }
    }
    
    pRoutingContext.next();
  }

}
