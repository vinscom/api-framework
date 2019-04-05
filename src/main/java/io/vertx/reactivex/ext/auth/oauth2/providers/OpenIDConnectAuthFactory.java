package io.vertx.reactivex.ext.auth.oauth2.providers;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.ext.auth.oauth2.OAuth2ClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class OpenIDConnectAuthFactory {

  private Vertx mVertx;
  private OAuth2Auth mAuthProvider;
  private Class<? extends OpenIDConnectAuth> mOpenIDConnectAuthClass;
  private OAuth2ClientOptions mOAuth2ClientOptions;
  private Logger mLog;

  public OAuth2Auth create() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    if (mAuthProvider == null) {
      Method m = getOpenIDConnectAuthClass().getMethod("rxDiscover", Vertx.class, OAuth2ClientOptions.class);
      @SuppressWarnings("unchecked")
			Single<OAuth2Auth> auth = (Single<OAuth2Auth>) m.invoke(null, getVertx(), getOAuth2ClientOptions());
      mAuthProvider = auth
              .subscribeOn(Schedulers.io())
              .doOnSuccess(a -> getLog().debug(() -> "AuthProvider Created Successfully"))
              .blockingGet();
    }
    return mAuthProvider;
  }

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

  public Class<? extends OpenIDConnectAuth> getOpenIDConnectAuthClass() {
    return mOpenIDConnectAuthClass;
  }

  public void setOpenIDConnectAuthClass(Class<? extends OpenIDConnectAuth> pOpenIDConnectAuthClass) {
    this.mOpenIDConnectAuthClass = pOpenIDConnectAuthClass;
  }

  public OAuth2ClientOptions getOAuth2ClientOptions() {
    return mOAuth2ClientOptions;
  }

  public void setOAuth2ClientOptions(OAuth2ClientOptions pOAuth2ClientOptions) {
    this.mOAuth2ClientOptions = pOAuth2ClientOptions;
  }

}
