package in.erail.common;

import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth;
import io.vertx.reactivex.redis.RedisClient;
import java.util.Optional;

/**
 *
 * @author vinay
 */
public class FrameworkConfiguration {
  
  private RedisClient mRedisClient;
  private OAuth2Auth mOAuth2Auth;

  public RedisClient getRedisClient() {
    return mRedisClient;
  }

  public void setRedisClient(RedisClient pRedisClient) {
    this.mRedisClient = pRedisClient;
  }

  public OAuth2Auth getOAuth2Auth() {
    return mOAuth2Auth;
  }

  public void setOAuth2Auth(OAuth2Auth pOAuth2Auth) {
    this.mOAuth2Auth = pOAuth2Auth;
  }
  

  public boolean isRedisEnable(){
    return Optional.ofNullable(getRedisClient()).isPresent();
  }
  
  public boolean isOAuth2AuthEnable(){
    return Optional.ofNullable(getOAuth2Auth()).isPresent();
  }
}
