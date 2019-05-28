package in.erail.route;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import in.erail.route.oidc.LocalAuthenticator;
import io.netty.handler.codec.http.HttpScheme;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

public class OIDCCallbackRouteBuilder extends AbstractRouterBuilderImpl {

  private String mCallbackURI;
  private AuthProvider mAuthProvider;
  private String mQueryParamAuthCode;
  private boolean mEnableProxy;
  private String mSuccessPath;
  private String mFailPath;
  private LocalAuthenticator mLocalAuthenticater;

  public void handle(RoutingContext pRoutingCoutext) {

    if (getAuthProvider() == null) {
      getLog().warn("Auth provider not set");
      return;
    }

    JsonObject tokenConfig = getTokenConfig(pRoutingCoutext);

    getAuthProvider()
            .rxAuthenticate(tokenConfig)
            .flatMapCompletable(u -> getLocalAuthenticater().authenticate(pRoutingCoutext, u))
            .doOnComplete(() -> {
              getLog().debug(() -> "Success URL:" + getSuccessURL(pRoutingCoutext));
              pRoutingCoutext
                      .response()
                      .putHeader(HttpHeaders.LOCATION, getSuccessURL(pRoutingCoutext))
                      .setStatusCode(302)
                      .end();
            })
            .doOnError((err) -> {
              getLog().error("Fail URL:" + getFailURL(pRoutingCoutext), err);
              pRoutingCoutext
                      .response()
                      .putHeader(HttpHeaders.LOCATION, getFailURL(pRoutingCoutext))
                      .setStatusCode(302)
                      .end();
            })
            .subscribe();
  }

  private String baseURL(RoutingContext pRoutingContext) {

    String host;
    String protocol;

    if (isEnableProxy()) {
      host = pRoutingContext.request().getHeader(HttpHeaders.X_FORWARDED_HOST);
      protocol = pRoutingContext.request().getHeader(HttpHeaders.X_FORWARDED_PROTO);
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

    JsonObject config = new JsonObject()
            .put("code", authCode)
            .put("redirect_uri", baseURL(pRoutingContext) + getCallbackURI());

    getLog().debug(() -> "Auth Config:" + config);

    return config;

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

  public AuthProvider getAuthProvider() {
    return mAuthProvider;
  }

  public void setAuthProvider(AuthProvider pAuthProvider) {
    this.mAuthProvider = pAuthProvider;
  }

  public LocalAuthenticator getLocalAuthenticater() {
    return mLocalAuthenticater;
  }

  public void setLocalAuthenticater(LocalAuthenticator pLocalAuthenticater) {
    this.mLocalAuthenticater = pLocalAuthenticater;
  }

}
