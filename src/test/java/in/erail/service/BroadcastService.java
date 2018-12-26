package in.erail.service;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import in.erail.test.TestConstants;
import io.vertx.core.json.JsonObject;
import in.erail.model.RequestEvent;
import in.erail.model.ResponseEvent;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Maybe;

/**
 *
 * @author vinay
 */
public class BroadcastService extends RESTServiceImpl {

  @Override
  public Maybe<ResponseEvent> process(RequestEvent pRequest) {

    String topicName = pRequest.getPathParameters().get(TestConstants.Service.Broadcast.APIMessage.PARAM_TOPIC_NAME);

    if (Strings.isNullOrEmpty(topicName)) {
      return Maybe.just(new ResponseEvent().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()));
    }

    JsonObject bodyJson = new JsonObject(pRequest.bodyAsString());

    getVertx()
            .eventBus()
            .publish(topicName, bodyJson);

    getLog().debug(() -> String.format("Message[%s] published on [%s]", bodyJson.toString(), topicName));

    ResponseEvent response = new ResponseEvent();
    response.setBody(TestConstants.Service.Message.successMessage().toString().getBytes());
    response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8);

    return Maybe.just(response);
  }

}
