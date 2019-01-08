package in.erail.service;

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

  Maybe<ResponseEvent> process(RequestEvent pRequest);

  String getAuthority();
  
  boolean isSecure();
}
