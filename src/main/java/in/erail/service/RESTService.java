package in.erail.service;


import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;

/**
 *
 * @author vinay
 */
public interface RESTService {
  String getOperationId();
  String getServiceUniqueId();
  void process(Message<JsonObject> pMessage);
}
