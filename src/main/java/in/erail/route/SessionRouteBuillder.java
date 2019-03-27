package in.erail.route;

import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CookieHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.sstore.SessionStore;

/**
 *
 * @author vinay
 */
public class SessionRouteBuillder extends AbstractRouterBuilderImpl {

  private SessionStore mSessionStore;
  private boolean mEnableSession = false;

  public SessionStore getSessionStore() {
    return mSessionStore;
  }

  public void setSessionStore(SessionStore pSessionStore) {
    this.mSessionStore = pSessionStore;
  }

  @Override
  public Router getRouter(Router pRouter) {
    Route route = pRouter.route();
    route.handler(BodyHandler.create());
    route.handler(CookieHandler.create());
    if (isEnableSession()) {
      route.handler(SessionHandler.create(getSessionStore()));
      getLog().info("Session Store Enabled");
    }
    return pRouter;
  }

  public boolean isEnableSession() {
    return mEnableSession;
  }

  public void setEnableSession(boolean pEnableSession) {
    this.mEnableSession = pEnableSession;
  }

}
