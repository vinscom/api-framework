package in.erail.auth;

import in.erail.model.RequestEvent;
import java.util.Optional;

/**
 *
 * @author vinay
 */
public class OAuth2Utils {

  public static Optional<String> getUserIdFromRequest(RequestEvent pRequest) {
    return Optional
            .ofNullable(pRequest.getPrincipal())
            .filter(t -> t.containsKey("sub"))
            .map(t -> (String) t.get("sub"));
  }

}
