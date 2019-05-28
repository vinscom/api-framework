package in.erail.route.oidc;

import in.erail.common.FrameworkConstants;
import io.reactivex.Completable;
import io.vertx.reactivex.ext.auth.User;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.Session;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class SessionAuthenticator implements LocalAuthenticator {

  private Logger mLog;

  @Override
  public Completable authenticate(RoutingContext pRoutingCoutext, User pUser) {
    Session session = pRoutingCoutext.session();

    if (session == null) {
      getLog().error(() -> "Session not found");
      return Completable.error(new RuntimeException("Session not found"));
    }

    session = session.regenerateId();
    session.put(FrameworkConstants.Session.PRINCIPAL, pUser.getDelegate());

    return Completable.complete();
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

}
