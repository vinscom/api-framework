package in.erail.service;

import com.google.common.base.Strings;
import in.erail.common.FrameworkConstants;
import in.erail.test.TestConstants;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;

/**
 *
 * @author vinay
 */
public class BroadcastServiceBodyAsBinary extends RESTServiceImpl{

  @Override
  public void process(Message<JsonObject> pMessage) {
    JsonObject param = pMessage.body().getJsonObject(FrameworkConstants.RoutingContext.Json.PATH_PARAM);
    String topicName = param.getString(TestConstants.Service.Broadcast.APIMessage.PARAM_TOPIC_NAME);

    if(Strings.isNullOrEmpty(topicName)){
      pMessage.fail(0, "Empty Topic Name");
    }

    byte[] body = pMessage.body().getBinary(FrameworkConstants.RoutingContext.Json.BODY);
    
    JsonObject resp = new JsonObject(Buffer.buffer(body));
    resp.put(FrameworkConstants.RoutingContext.Json.BODY, TestConstants.Service.Message.successMessage());
    
    pMessage.reply(resp);
  }
  
}
