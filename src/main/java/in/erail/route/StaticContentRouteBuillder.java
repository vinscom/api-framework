package in.erail.route;

import com.google.common.base.Strings;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.StaticHandler;

/**
 *
 * @author vinay
 */
public class StaticContentRouteBuillder extends AbstractRouterBuilderImpl {

  private String mWebRoot;
  private boolean mCachingEnabled = false;
  
  @Override
  public Router getRouter(Router pRouter) {
    Route route = pRouter.route();
    if(Strings.isNullOrEmpty(getWebRoot())){
      route.handler(StaticHandler.create().setCachingEnabled(isCachingEnabled()));
    } else {
      route.handler(StaticHandler.create(getWebRoot()).setCachingEnabled(isCachingEnabled()));
    }
    return pRouter;
  }

  public String getWebRoot() {
    return mWebRoot;
  }

  public void setWebRoot(String pWebRoot) {
    this.mWebRoot = pWebRoot;
  }

  public boolean isCachingEnabled() {
    return mCachingEnabled;
  }

  public void setCachingEnabled(boolean pCachingEnabled) {
    this.mCachingEnabled = pCachingEnabled;
  }

}
