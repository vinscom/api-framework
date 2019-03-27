package io.vertx.ext.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author vinay
 */
public class DummyAuthProvider implements AuthProvider {

  @Override
  public void authenticate(JsonObject pAuthInfo, Handler<AsyncResult<User>> pResultHandler) {
    pResultHandler.handle(Future.succeededFuture(new DummyUser()));
  }

  public static class DummyUser extends AbstractUser {

    @Override
    protected void doIsPermitted(String pErmission, Handler<AsyncResult<Boolean>> pResultHandler) {
      pResultHandler.handle(Future.succeededFuture(Boolean.TRUE));
    }

    @Override
    public JsonObject principal() {
      return new JsonObject();
    }

    @Override
    public void setAuthProvider(AuthProvider pAuthProvider) {

    }

  }
}
