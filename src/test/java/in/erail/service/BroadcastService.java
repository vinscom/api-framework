package in.erail.service;

import com.google.common.base.Strings;
import in.erail.common.FrameworkConstants;
import in.erail.test.TestConstants;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;

/**
 *
 * @author vinay
 */
public class BroadcastService extends RESTServiceImpl{

  @Override
  public void process(Message<JsonObject> pMessage) {
    JsonObject param = pMessage.body().getJsonObject(FrameworkConstants.RoutingContext.Json.PATH_PARAM);
    String topicName = param.getString(TestConstants.Service.Broadcast.APIMessage.PARAM_TOPIC_NAME);

    if(Strings.isNullOrEmpty(topicName)){
      pMessage.fail(0, "Empty Topic Name");
    }

    JsonObject body = pMessage.body().getJsonObject(FrameworkConstants.RoutingContext.Json.BODY);
    
    getVertx()
            .eventBus()
            .publish(topicName, body);
    
    getLog().debug(() -> String.format("Message[%s] published on [%s]", body.toString(),topicName));
    
    JsonObject resp = new JsonObject();
    resp.put(FrameworkConstants.RoutingContext.Json.BODY, TestConstants.Service.Message.successMessage());
    
    pMessage.reply(resp);
  }
  
}
