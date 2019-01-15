package in.erail.service;

import com.google.common.base.Strings;
import com.google.common.net.MediaType;
import in.erail.model.Event;

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
  public MaybeSource<Event> process(Maybe<Event> pRequest) {
    return pRequest.doOnSuccess(e -> handle(e.getRequest(), e.getResponse()));
  }

  protected void handle(RequestEvent pRequest, ResponseEvent pRespone) {
    String topicName = pRequest.getPathParameters().get(TestConstants.Service.Broadcast.APIMessage.PARAM_TOPIC_NAME);

    if (Strings.isNullOrEmpty(topicName)) {
      pRespone.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
      return;
    }

    JsonObject bodyJson = new JsonObject(pRequest.bodyAsString());

    getVertx()
            .eventBus()
            .publish(topicName, bodyJson);

    getLog().debug(() -> String.format("Message[%s] published on [%s]", bodyJson.toString(), topicName));

    pRespone.setBody(TestConstants.Service.Message.successMessage().toString().getBytes());
    pRespone.setMediaType(MediaType.JSON_UTF_8);
  }
}
