package in.erail.service;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import in.erail.model.RequestEvent;
import in.erail.model.ResponseEvent;
import in.erail.test.TestConstants;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;

/**
 *
 * @author vinay
 */
public class BinaryBodyService extends RESTServiceImpl {

  @Override
  public ResponseEvent process(RequestEvent pRequest) {
    String topicName = pRequest.getPathParameters().get(TestConstants.Service.Broadcast.APIMessage.PARAM_TOPIC_NAME);

    if (Strings.isNullOrEmpty(topicName)) {
      return new ResponseEvent()
              .setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
    }

    ResponseEvent response = new ResponseEvent();
    response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8);

    JsonObject jsonBody = new JsonObject(Buffer.buffer(pRequest.getBody()));

    String bodyContent = jsonBody.getString("data");
    response.setBody(bodyContent.getBytes());
    return response;
  }

}
