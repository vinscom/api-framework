package in.erail.route;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import in.erail.common.FrameworkConstants;
import io.netty.handler.codec.http.HttpScheme;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.Session;

public class OIDCCallbackRouteBuilder extends AbstractRouterBuilderImpl {

  private String mCallbackURI;
  private OAuth2Auth mOAuth2Auth;
  private String mQueryParamAuthCode;
  private boolean mEnableProxy;
  private String mSuccessPath;
  private String mFailPath;

  public void handle(RoutingContext pRoutingCoutext) {
    JsonObject tokenConfig = getTokenConfig(pRoutingCoutext);
    getOAuth2Auth().getDelegate().authenticate(tokenConfig, (response) -> {
      if (response.succeeded()) {

        Session session = pRoutingCoutext.session().regenerateId();
        session.put(FrameworkConstants.Session.PRINCIPAL, response.result().principal());

        getLog().debug(() -> "Success URL:" + getSuccessURL(pRoutingCoutext));

        pRoutingCoutext
                .response()
                .putHeader(HttpHeaders.LOCATION, getSuccessURL(pRoutingCoutext))
                .setStatusCode(302)
                .end();
      } else {
        getLog().error(response.cause());

        getLog().debug(() -> "Fail URL:" + getFailURL(pRoutingCoutext));

        pRoutingCoutext
                .response()
                .putHeader(HttpHeaders.LOCATION, getFailURL(pRoutingCoutext))
                .setStatusCode(302)
                .end();
      }
    });
  }

  private String baseURL(RoutingContext pRoutingContext) {

    String host;
    String protocol;

    if (isEnableProxy()) {
      host = pRoutingContext.request().getHeader("X-Forwarded-Host");
      protocol = pRoutingContext.request().getHeader("X-Forwarded-Proto");
    } else {
      host = pRoutingContext.request().getHeader("Host");
      protocol = HttpScheme.HTTP.name().toString();
    }

    String url = protocol + "://" + host;

    return url;
  }

  private String getSuccessURL(RoutingContext pRoutingContext) {
    StringBuffer url = new StringBuffer();
    url.append(url);
    if (!Strings.isNullOrEmpty(getSuccessPath())) {
      if (!getSuccessPath().startsWith("/")) {
        url.append("/");
      }
      url.append(getSuccessPath());
    }
    url.append("?login=success");
    return url.toString();
  }

  private String getFailURL(RoutingContext pRoutingContext) {

    StringBuffer url = new StringBuffer();
    url.append(url);
    if (!Strings.isNullOrEmpty(getFailPath())) {
      if (!getFailPath().startsWith("/")) {
        url.append("/");
      }
      url.append(getFailPath());
    }
    url.append("?login=fail");
    return url.toString();
  }

  private JsonObject getTokenConfig(RoutingContext pRoutingContext) {

    String authCode = pRoutingContext.request().params().get(getQueryParamAuthCode());

    return new JsonObject()
            .put("code", authCode)
            .put("redirect_uri", baseURL(pRoutingContext) + getCallbackURI());
  }

  public OAuth2Auth getOAuth2Auth() {
    return mOAuth2Auth;
  }

  public void setOAuth2Auth(OAuth2Auth pOAuth2Auth) {
    this.mOAuth2Auth = pOAuth2Auth;
  }

  public String getQueryParamAuthCode() {
    return mQueryParamAuthCode;
  }

  public void setQueryParamAuthCode(String pQueryParamAuthCode) {
    this.mQueryParamAuthCode = pQueryParamAuthCode;
  }

  public String getCallbackURI() {
    return mCallbackURI;
  }

  public void setCallbackURI(String pCallbackURI) {
    this.mCallbackURI = pCallbackURI;
  }

  @Override
  public Router getRouter(Router pRouter) {
    pRouter.route(getCallbackURI()).handler(this::handle);
    return pRouter;
  }

  public boolean isEnableProxy() {
    return mEnableProxy;
  }

  public void setEnableProxy(boolean pEnableProxy) {
    this.mEnableProxy = pEnableProxy;
  }

  public String getSuccessPath() {
    return mSuccessPath;
  }

  public void setSuccessPath(String pSuccessPath) {
    this.mSuccessPath = pSuccessPath;
  }

  public String getFailPath() {
    return mFailPath;
  }

  public void setFailPath(String pFailPath) {
    this.mFailPath = pFailPath;
  }

}
