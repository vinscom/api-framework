package in.erail.service;


import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;

/**
 *
 * @author vinay
 */
public interface Service {
  String getOperationId();
  String getServiceUniqueId();
  void process(Message<JsonObject> pMessage);
}
