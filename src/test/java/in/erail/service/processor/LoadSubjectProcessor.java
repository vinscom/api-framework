package in.erail.service.processor;

import in.erail.model.Event;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import java.util.Optional;

/**
 *
 * @author vinay
 */
public class LoadSubjectProcessor implements MaybeTransformer<Event, Event> {

  private String mMessage;

  @Override
  public MaybeSource<Event> apply(Maybe<Event> pEvent) {
    return pEvent.map(e -> {
      e.getRequest().setSubject(Optional.ofNullable(e.getRequest().getSubject()).orElse("") + getMessage());
      return e;
    });
  }

  public String getMessage() {
    return mMessage;
  }

  public void setMessage(String pMessage) {
    this.mMessage = pMessage;
  }

}
