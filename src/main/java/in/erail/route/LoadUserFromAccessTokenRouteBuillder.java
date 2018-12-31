package in.erail.route;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import in.erail.user.UserProvider;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vinay
 */
public class LoadUserFromAccessTokenRouteBuillder extends AbstractRouterBuilderImpl {

  private Pattern AUTH_TOKEN = Pattern.compile("^Bearer\\s(?<token>.*)");
  private UserProvider mUserProvider;

  @Override
  public Router getRouter(Router pRouter) {
    pRouter.route().handler(this::handle);
    return pRouter;
  }

  public void handle(RoutingContext pRoutingContext) {

    if (pRoutingContext.user() == null) {
      String access_token = pRoutingContext.request().getHeader(HttpHeaders.AUTHORIZATION);
      if (!Strings.isNullOrEmpty(access_token)) {
        Matcher token = AUTH_TOKEN.matcher(access_token);
        if (token.find()) {
          JsonObject accessToken = new JsonObject().put("access_token", token.group("token"));
          try {
            pRoutingContext
                    .setUser(getUserProvider()
                            .getUser(accessToken)
                            .blockingGet());
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

  public UserProvider getUserProvider() {
    return mUserProvider;
  }

  public void setUserProvider(UserProvider pUserProvider) {
    this.mUserProvider = pUserProvider;
  }

}
