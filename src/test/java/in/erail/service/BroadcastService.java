package in.erail.service;

import com.google.common.base.Strings;
import com.google.common.net.MediaType;

import in.erail.model.RequestEvent;
import in.erail.model.ResponseEvent;
import in.erail.test.TestConstants;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author vinay
 */
public class BroadcastService extends RESTServiceImpl {

  @Override
  public MaybeSource<ResponseEvent> process(Maybe<RequestEvent> pRequest) {
    return pRequest.map(this::handle);
  }

  protected ResponseEvent handle(RequestEvent pRequest) {
    String topicName = pRequest.getPathParameters().get(TestConstants.Service.Broadcast.APIMessage.PARAM_TOPIC_NAME);

    if (Strings.isNullOrEmpty(topicName)) {
      return new ResponseEvent().setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
    }

    JsonObject bodyJson = new JsonObject(pRequest.bodyAsString());

    getVertx()
            .eventBus()
            .publish(topicName, bodyJson);

    getLog().debug(() -> String.format("Message[%s] published on [%s]", bodyJson.toString(), topicName));

    ResponseEvent response = new ResponseEvent();
    response.setBody(TestConstants.Service.Message.successMessage().toString().getBytes());
    response.setMediaType(MediaType.JSON_UTF_8);

    return response;
  }
}
