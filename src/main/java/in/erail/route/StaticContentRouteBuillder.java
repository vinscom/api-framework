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
  
  @Override
  public Router getRouter(Router pRouter) {
    Route route = pRouter.route();
    if(Strings.isNullOrEmpty(getWebRoot())){
      route.handler(StaticHandler.create());
    } else {
      route.handler(StaticHandler.create(getWebRoot()));
    }
    return pRouter;
  }

  public String getWebRoot() {
    return mWebRoot;
  }

  public void setWebRoot(String pWebRoot) {
    this.mWebRoot = pWebRoot;
  }

}
