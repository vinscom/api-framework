package in.erail.service;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import in.erail.test.TestConstants;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import static in.erail.common.FrameworkConstants.RoutingContext.*;
import in.erail.model.ReqestEvent;
import in.erail.model.ResponseEvent;

/**
 *
 * @author vinay
 */
public class BinaryBodyService extends RESTServiceImpl{

  @Override
  public void process(Message<JsonObject> pMessage) {
    
    ReqestEvent request = pMessage.body().mapTo(ReqestEvent.class);
    
    String topicName = request.getPathParameters().get(TestConstants.Service.Broadcast.APIMessage.PARAM_TOPIC_NAME);
    
    if(Strings.isNullOrEmpty(topicName)){
      pMessage.fail(0, "Empty Topic Name");
    }

    ResponseEvent response = new ResponseEvent();
    response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8);
    
    JsonObject jsonBody = new JsonObject(Buffer.buffer(request.getBody()));
    
    String bodyContent = jsonBody.getString("data");
    response.setBody(bodyContent.getBytes());
    
    pMessage.reply(JsonObject.mapFrom(response));
  }
  
}
