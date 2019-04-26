package in.erail.health;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author vinay
 */
public class SimpleHealthStatus implements HealthStatus {

  private final JsonObject status = new JsonObject().put("SimpleHealthStatus", "success");
  
  @Override
  public Object healthStatus() {
    return status;
  }
  
}
