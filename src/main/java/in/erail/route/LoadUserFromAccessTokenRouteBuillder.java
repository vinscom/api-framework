package in.erail.route;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.impl.AccessTokenImpl;
import io.vertx.ext.auth.oauth2.impl.OAuth2AuthProviderImpl;
import io.vertx.reactivex.ext.auth.oauth2.AccessToken;
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 *
 * @author vinay
 */
public class LoadUserFromAccessTokenRouteBuillder extends AbstractRouterBuilderImpl {

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
    if (pRoutingContext.user() == null) {
      String access_token = pRoutingContext.request().getHeader(HttpHeaders.AUTHORIZATION);
      if (!Strings.isNullOrEmpty(access_token)) {
        OAuth2AuthProviderImpl provider = (OAuth2AuthProviderImpl) getOAuth2Auth().getDelegate();
        JsonObject accessToken = new JsonObject().put("access_token", access_token.split(" ")[1]);
        try {
          AccessTokenImpl token = new AccessTokenImpl(provider, accessToken);
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
