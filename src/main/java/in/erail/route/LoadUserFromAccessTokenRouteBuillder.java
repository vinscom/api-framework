package in.erail.route;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vinay
 */
public class LoadUserFromAccessTokenRouteBuillder extends AbstractRouterBuilderImpl {

  private final Pattern AUTH_TOKEN = Pattern.compile("^Bearer\\s(?<token>.*)");
  private AuthProvider mAuthProvider;

  @Override
  public Router getRouter(Router pRouter) {
    pRouter.route().handler(this::handle);
    return pRouter;
  }

  public void handle(RoutingContext pRoutingContext) {

    if (pRoutingContext.user() == null) {
      String access_token = pRoutingContext.request().getHeader(HttpHeaders.AUTHORIZATION);
      if (!Strings.isNullOrEmpty(access_token)) {
        Matcher tokenRegex = AUTH_TOKEN.matcher(access_token);
        if (tokenRegex.find()) {
          String token = tokenRegex.group("token");
          JsonObject authInfo = new JsonObject()
                  .put("access_token", token)
                  .put("token_type", "Bearer")
                  .put("jwt", token);
          try {
            pRoutingContext.setUser(getAuthProvider().rxAuthenticate(authInfo).blockingGet());
          } catch (RuntimeException e) {
            getLog().error(e);
            pRoutingContext.fail(401);
            return;
          }
        } else {
          getLog().warn(() -> "Invalid Auth Header:" + access_token);
        }
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
