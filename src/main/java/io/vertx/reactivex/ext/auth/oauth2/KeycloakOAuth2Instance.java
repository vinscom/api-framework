package io.vertx.reactivex.ext.auth.oauth2;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.oauth2.providers.KeycloakAuth;
import org.apache.logging.log4j.Logger;
import in.erail.glue.annotation.StartService;

/**
 *
 * @author vinay
 */
public class KeycloakOAuth2Instance {

  private OAuth2Auth mOAuth2Auth;
  private Vertx mVertx;
  private OAuth2FlowType mOAuth2FlowType;
  private JsonObject mConfig;
  private Logger mLog;
  
  @StartService
  public void start() {
    setOAuth2Auth(KeycloakAuth.create(getVertx(), getOAuth2FlowType(), getConfig()));
  }

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  public OAuth2FlowType getOAuth2FlowType() {
    return mOAuth2FlowType;
  }

  public void setOAuth2FlowType(OAuth2FlowType pOAuth2FlowType) {
    this.mOAuth2FlowType = pOAuth2FlowType;
  }

  public JsonObject getConfig() {
    return mConfig;
  }

  public void setConfig(JsonObject pConfig) {
    this.mConfig = pConfig;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public OAuth2Auth getOAuth2Auth() {
    return mOAuth2Auth;
  }

  public void setOAuth2Auth(OAuth2Auth pOAuth2Auth) {
    this.mOAuth2Auth = pOAuth2Auth;
  }

}
