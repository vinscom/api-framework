package in.erail.user;

import io.reactivex.Maybe;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.auth.User;

/**
 *
 * @author vinay
 */
public interface UserProvider {

  default Maybe<User> getUser(JsonObject pPrincipal) {
    return Maybe.empty();
  }
}
