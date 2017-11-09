package in.erail.route;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import org.apache.logging.log4j.Logger;

public abstract class AbstractRouterBuilderImpl implements RouterBuilder {

  private Vertx mVertx;
  private Logger mLog;
  private boolean mEnable = true;

  @Override
  public Router getRouter() {

    Router router = Router.router(getVertx());

    if (isEnable()) {
      return getRouter(router);
    }
    return router;
  }

  public abstract Router getRouter(Router pRouter);

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public boolean isEnable() {
    return mEnable;
  }

  public void setEnable(boolean pEnable) {
    this.mEnable = pEnable;
  }
}
