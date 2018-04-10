package in.erail.route;

import in.erail.common.FrameworkConstants;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.impl.OAuth2AuthProviderImpl;
import io.vertx.ext.auth.oauth2.impl.OAuth2TokenImpl;
import io.vertx.reactivex.ext.auth.oauth2.AccessToken;
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.Session;

/**
 *
 * @author vinay
 */
public class LoadUserFromSessionRouteBuillder extends AbstractRouterBuilderImpl {

  private OAuth2Auth mOAuth2Auth;

  public OAuth2Auth getOAuth2Auth() {
    return mOAuth2Auth;
  }

  public void setOAuth2Auth(OAuth2Auth pOAuth2Auth) {
    this.mOAuth2Auth = pOAuth2Auth;
  }

  @Override
  public Router getRouter(Router pRouter) {
    pRouter.route().handler(this::handle);
    return pRouter;
  }

  public void handle(RoutingContext pRoutingContext) {
    Session session = pRoutingContext.session();
    if (session != null && getOAuth2Auth() != null) {
      JsonObject principal = session.get(FrameworkConstants.Session.PRINCIPAL);
      if (principal != null) {
        OAuth2AuthProviderImpl provider = (OAuth2AuthProviderImpl) getOAuth2Auth().getDelegate();
        try {
          OAuth2TokenImpl token = new OAuth2TokenImpl(provider, principal);
          pRoutingContext.setUser(new AccessToken(token));
        } catch (RuntimeException e) {
          getLog().error(e);
          pRoutingContext.fail(401);
          return;
        }
      }
    }
    pRoutingContext.next();
  }
}
