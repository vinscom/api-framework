package in.erail.route.oidc;

import io.reactivex.Completable;
import io.vertx.reactivex.ext.auth.User;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 *
 * @author vinay
 */
public interface LocalAuthenticator {

  Completable authenticate(RoutingContext pContext, User pUser);
}
