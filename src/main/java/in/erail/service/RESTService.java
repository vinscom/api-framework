package in.erail.service;

import in.erail.glue.common.Util;
import in.erail.model.Event;
import in.erail.model.RequestEvent;
import in.erail.model.ResponseEvent;
import io.reactivex.Maybe;

/**
 *
 * @author vinay
 */
public interface RESTService {

  String getOperationId();

  String getServiceUniqueId();

  default Class<? extends RequestEvent> getRequestEventClass() {
    return RequestEvent.class;
  }

  default Class<? extends ResponseEvent> getResponseEventClass() {
    return ResponseEvent.class;
  }

  default Event createEvent(RequestEvent pRequest) throws InstantiationException, IllegalAccessException {
    return new Event(pRequest, Util.createInstance(getResponseEventClass()));
  }

  Maybe<Event> handleEvent(Event pEvent);

  String[] getAuthority();

  boolean isSecure();
}
