package in.erail.service.processor;

import java.util.Optional;

import in.erail.model.Event;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;

/**
 *
 * @author vinay
 */
public class AddHeaderProcessor implements MaybeTransformer<Event, Event> {

  private String mMessage;

  @Override
  public MaybeSource<Event> apply(Maybe<Event> pEvent) {
    return pEvent.map(r -> {
      String msg = Optional.ofNullable(r.getResponse().headerValue("ProcessorHeader")).orElse("") + getMessage();
      r.getResponse().removeHeader("ProcessorHeader");
      r.getResponse().addHeader("ProcessorHeader", msg);
      return r;
    });
  }

  public String getMessage() {
    return mMessage;
  }

  public void setMessage(String pMessage) {
    this.mMessage = pMessage;
  }

}
