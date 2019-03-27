package in.erail.route;

import in.erail.common.FrameworkConstants;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.auth.User;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.Session;

/**
 *
 * @author vinay
 */
public class LoadUserFromSessionRouteBuillder extends AbstractRouterBuilderImpl {

	private AuthProvider mAuthProvider;
	
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
        user.setAuthProvider(getAuthProvider().getDelegate());
        pRoutingContext.setUser(new User(user));
      }
    }
    
    pRoutingContext.next();
  }

  public AuthProvider getAuthProvider() {
    return mAuthProvider;
  }

  public void setAuthProvider(AuthProvider pAuthProvider) {
    this.mAuthProvider = pAuthProvider;
  }

}
