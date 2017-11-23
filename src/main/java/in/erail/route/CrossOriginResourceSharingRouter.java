package in.erail.route;

import com.google.common.base.Strings;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 *
 * @author vinay
 */
public class CrossOriginResourceSharingRouter extends AbstractRouterBuilderImpl {

  private String mAccessControlAllowOrigin;
  private String mAccessControlAllowMethods;
  private String mAccessControlAllowHeaders;
  private String mAccessControlMaxAge;

  public void handler(RoutingContext pContext) {

    HttpServerResponse resp = pContext.response();

    if (!Strings.isNullOrEmpty(getAccessControlAllowHeaders())) {
      resp.putHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString(), getAccessControlAllowHeaders());
    }

    if (!Strings.isNullOrEmpty(getAccessControlAllowMethods())) {
      resp.putHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS.toString(), getAccessControlAllowMethods());
    }

    if (!Strings.isNullOrEmpty(getAccessControlAllowOrigin())) {
      resp.putHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString(), getAccessControlAllowOrigin());
    }

    if (!Strings.isNullOrEmpty(getAccessControlMaxAge())) {
      resp.putHeader(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE.toString(), getAccessControlMaxAge());
    }

    pContext.response().end();
  }

  public String getAccessControlAllowOrigin() {
    return mAccessControlAllowOrigin;
  }

  public void setAccessControlAllowOrigin(String pAccessControlAllowOrigin) {
    this.mAccessControlAllowOrigin = pAccessControlAllowOrigin;
  }

  public String getAccessControlAllowMethods() {
    return mAccessControlAllowMethods;
  }

  public void setAccessControlAllowMethods(String pAccessControlAllowMethods) {
    this.mAccessControlAllowMethods = pAccessControlAllowMethods;
  }

  public String getAccessControlAllowHeaders() {
    return mAccessControlAllowHeaders;
  }

  public void setAccessControlAllowHeaders(String pAccessControlAllowHeaders) {
    this.mAccessControlAllowHeaders = pAccessControlAllowHeaders;
  }

  public String getAccessControlMaxAge() {
    return mAccessControlMaxAge;
  }

  public void setAccessControlMaxAge(String pAccessControlMaxAge) {
    this.mAccessControlMaxAge = pAccessControlMaxAge;
  }

  @Override
  public Router getRouter(Router pRouter) {
    pRouter.options().handler(this::handler);
    return pRouter;
  }

}
