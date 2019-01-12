package in.erail.service.processor;

import in.erail.model.RequestEvent;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import java.util.Optional;

/**
 *
 * @author vinay
 */
public class LoadSubjectProcessor implements MaybeTransformer<RequestEvent, RequestEvent> {

  private String mMessage;

  @Override
  public MaybeSource<RequestEvent> apply(Maybe<RequestEvent> pRequest) {
    return pRequest.map(e -> e.setSubject(Optional.ofNullable(e.getSubject()).orElse("") + getMessage()));
  }

  public String getMessage() {
    return mMessage;
  }

  public void setMessage(String pMessage) {
    this.mMessage = pMessage;
  }

}
