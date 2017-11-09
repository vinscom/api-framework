package in.erail.route;

import com.google.common.net.HttpHeaders;
import in.erail.common.FramworkConstants;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.Session;

public class OIDCCallbackRouteBuilder extends AbstractRouterBuilderImpl {

  private String mCallbackURI;
  private OAuth2Auth mOAuth2Auth;
  private String mQueryParamAuthCode;

  public void handle(RoutingContext pRoutingCoutext) {
    JsonObject tokenConfig = getTokenConfig(pRoutingCoutext);
    getOAuth2Auth().getDelegate().authenticate(tokenConfig, (response) -> {
      if (response.succeeded()) {

        Session session = pRoutingCoutext.session().regenerateId();
        session.put(FramworkConstants.Session.PRINCIPAL, response.result().principal());

        pRoutingCoutext
                .response()
                .putHeader(HttpHeaders.LOCATION, getSuccessURL(pRoutingCoutext))
                .setStatusCode(302)
                .end();
      } else {
        getLog().error(response.cause());
        pRoutingCoutext
                .response()
                .putHeader(HttpHeaders.LOCATION, getFailURL(pRoutingCoutext))
                .setStatusCode(302)
                .end();
      }
    });
  }

  private String baseURL(RoutingContext pRoutingContext) {
    String host = pRoutingContext.request().getHeader("Host");
    String protocol = pRoutingContext.request().getHeader("X-Forwarded-Proto");
    return protocol + "://" + host;
  }

  private String getSuccessURL(RoutingContext pRoutingContext) {
    return baseURL(pRoutingContext) + "?login=success";
  }

  private String getFailURL(RoutingContext pRoutingContext) {
    return baseURL(pRoutingContext) + "?login=fail";
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
}
