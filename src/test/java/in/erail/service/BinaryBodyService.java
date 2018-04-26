package in.erail.service;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import in.erail.test.TestConstants;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import static in.erail.common.FrameworkConstants.RoutingContext.*;

/**
 *
 * @author vinay
 */
public class BinaryBodyService extends RESTServiceImpl{

  @Override
  public void process(Message<JsonObject> pMessage) {
    JsonObject param = pMessage.body().getJsonObject(Json.PATH_PARAM);
    String topicName = param.getString(TestConstants.Service.Broadcast.APIMessage.PARAM_TOPIC_NAME);

    if(Strings.isNullOrEmpty(topicName)){
      pMessage.fail(0, "Empty Topic Name");
    }

    JsonObject headers = new JsonObject();
    headers.put(HttpHeaders.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8.toString());
    
    byte[] body = pMessage.body().getBinary(Json.BODY);
    JsonObject jsonBody = new JsonObject(Buffer.buffer(body));
    
    String bodyContent = jsonBody.getString("data");
    
    JsonObject resp = new JsonObject();
    resp.put(Json.BODY, bodyContent.getBytes());
    resp.put(Json.HEADERS, headers);
    
    pMessage.reply(resp);
  }
  
}
