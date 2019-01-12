package in.erail.service;

import com.google.common.net.MediaType;

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
  public MaybeSource<ResponseEvent> process(Maybe<RequestEvent> pRequest) {
    return pRequest.map(this::handle);
  }

  protected ResponseEvent handle(RequestEvent pRequest) {
    ResponseEvent response = new ResponseEvent();
    response.setMediaType(MediaType.PLAIN_TEXT_UTF_8);
    return response.setBody(pRequest.getSubject().toString().getBytes());
  }
}
