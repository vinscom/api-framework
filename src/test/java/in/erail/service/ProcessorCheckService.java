package in.erail.service;

import com.google.common.net.MediaType;
import in.erail.model.Event;

import in.erail.model.RequestEvent;
import in.erail.model.ResponseEvent;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;

/**
 *
 * @author vinay
 */
public class ProcessorCheckService extends RESTServiceImpl {

@Override
  public MaybeSource<Event> process(Maybe<Event> pRequest) {
    return pRequest.doOnSuccess(e -> handle(e.getRequest(), e.getResponse()));
  }

  protected void handle(RequestEvent pRequest, ResponseEvent pRespone) {
    pRespone.setMediaType(MediaType.PLAIN_TEXT_UTF_8);
    pRespone.setBody(pRequest.getSubject().toString().getBytes());
  }
}
