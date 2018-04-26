package in.erail.service;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import in.erail.common.FrameworkConstants;
import in.erail.test.TestConstants;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import static in.erail.common.FrameworkConstants.RoutingContext.*;

/**
 *
 * @author vinay
 */
public class BroadcastService extends RESTServiceImpl {

  @Override
  public void process(Message<JsonObject> pMessage) {
    JsonObject param = pMessage.body().getJsonObject(Json.PATH_PARAM);
    String topicName = param.getString(TestConstants.Service.Broadcast.APIMessage.PARAM_TOPIC_NAME);

    if (Strings.isNullOrEmpty(topicName)) {
      pMessage.fail(0, "Empty Topic Name");
    }

    byte[] body = pMessage.body().getBinary(FrameworkConstants.RoutingContext.Json.BODY);
    JsonObject bodyJson = new JsonObject(Buffer.buffer(body));

    getVertx()
            .eventBus()
            .publish(topicName, bodyJson);

    getLog().debug(() -> String.format("Message[%s] published on [%s]", bodyJson.toString(), topicName));

    JsonObject resp = new JsonObject();
    resp.put(Json.BODY, TestConstants.Service.Message.successMessage().toString().getBytes());

    JsonObject headers = new JsonObject().put(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
    resp.put(Json.HEADERS, headers);
    
    pMessage.reply(resp);
  }

}
