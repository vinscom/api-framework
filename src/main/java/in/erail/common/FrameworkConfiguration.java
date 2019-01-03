package in.erail.common;

import io.vertx.reactivex.redis.RedisClient;
import java.util.Optional;

/**
 *
 * @author vinay
 */
public class FrameworkConfiguration {
  
  private RedisClient mRedisClient;

  public RedisClient getRedisClient() {
    return mRedisClient;
  }

  public void setRedisClient(RedisClient pRedisClient) {
    this.mRedisClient = pRedisClient;
  }

  public boolean isRedisEnable(){
    return Optional.ofNullable(getRedisClient()).isPresent();
  }
  
}
