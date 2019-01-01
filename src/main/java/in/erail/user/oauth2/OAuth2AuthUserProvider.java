package in.erail.user.oauth2;

import in.erail.user.UserProvider;
import io.reactivex.Maybe;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.impl.OAuth2AuthProviderImpl;
import io.vertx.ext.auth.oauth2.impl.OAuth2TokenImpl;
import io.vertx.reactivex.ext.auth.User;
import io.vertx.reactivex.ext.auth.oauth2.AccessToken;
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth;

/**
 *
 * @author vinay
 */
public class OAuth2AuthUserProvider implements UserProvider {

  private OAuth2Auth mOAuth2Auth;

  @Override
  public Maybe<User> getUser(JsonObject pPrincipal) {
    OAuth2AuthProviderImpl provider = (OAuth2AuthProviderImpl) getOAuth2Auth().getDelegate();
    OAuth2TokenImpl token = new OAuth2TokenImpl(provider, pPrincipal);
    return Maybe.just(new AccessToken(token));
  }

  public OAuth2Auth getOAuth2Auth() {
    return mOAuth2Auth;
  }

  public void setOAuth2Auth(OAuth2Auth pOAuth2Auth) {
    this.mOAuth2Auth = pOAuth2Auth;
  }

}
