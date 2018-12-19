package in.erail.service;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import in.erail.test.TestConstants;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import in.erail.model.ReqestEvent;
import in.erail.model.ResponseEvent;

/**
 *
 * @author vinay
 */
public class BroadcastService extends RESTServiceImpl {

  @Override
  public void process(Message<JsonObject> pMessage) {
    
    ReqestEvent request = pMessage.body().mapTo(ReqestEvent.class);
    
    String topicName = request.getPathParameters().get(TestConstants.Service.Broadcast.APIMessage.PARAM_TOPIC_NAME);

    if (Strings.isNullOrEmpty(topicName)) {
      pMessage.fail(0, "Empty Topic Name");
    }

    JsonObject bodyJson = new JsonObject(request.bodyAsString());

    getVertx()
            .eventBus()
            .publish(topicName, bodyJson);

    getLog().debug(() -> String.format("Message[%s] published on [%s]", bodyJson.toString(), topicName));

    ResponseEvent response = new ResponseEvent();
    response.setBody(TestConstants.Service.Message.successMessage().toString().getBytes());
    response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8);
    
    pMessage.reply(JsonObject.mapFrom(response));
  }

}
