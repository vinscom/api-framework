package in.erail.service;

import com.google.common.base.Strings;
import com.google.common.net.MediaType;

import in.erail.model.RequestEvent;
import in.erail.model.ResponseEvent;
import in.erail.test.TestConstants;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author vinay
 */
public class BinaryBodyService extends RESTServiceImpl {

  @Override
  public MaybeSource<ResponseEvent> process(Maybe<RequestEvent> pRequest) {
    return pRequest.map(this::handle);
  }

  protected ResponseEvent handle(RequestEvent pRequest) {
    String topicName = pRequest.getPathParameters().get(TestConstants.Service.Broadcast.APIMessage.PARAM_TOPIC_NAME);

    if (Strings.isNullOrEmpty(topicName)) {
      return new ResponseEvent().setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
    }

    ResponseEvent response = new ResponseEvent();
    response.setMediaType(MediaType.PLAIN_TEXT_UTF_8);

    JsonObject jsonBody = new JsonObject(Buffer.buffer(pRequest.getBody()));

    String bodyContent = jsonBody.getString("data");
    return response.setBody(bodyContent.getBytes());
  }
}
