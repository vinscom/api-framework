package in.erail.service.processor;

import in.erail.model.ResponseEvent;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import java.util.Optional;

/**
 *
 * @author vinay
 */
public class AddHeaderProcessor implements MaybeTransformer<ResponseEvent, ResponseEvent> {

  private String mMessage;

  @Override
  public MaybeSource<ResponseEvent> apply(Maybe<ResponseEvent> pResponse) {
    return pResponse.map(r -> {
      String msg = Optional.ofNullable(r.headerValue("ProcessorHeader")).orElse("") + getMessage();
      r.removeHeader("ProcessorHeader");
      return r.addHeader("ProcessorHeader", msg);
    });
  }

  public String getMessage() {
    return mMessage;
  }

  public void setMessage(String pMessage) {
    this.mMessage = pMessage;
  }

}
