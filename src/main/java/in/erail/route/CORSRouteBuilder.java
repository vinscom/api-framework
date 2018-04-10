package in.erail.route;

import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.CorsHandler;
import java.util.Arrays;
import java.util.Set;

/**
 *
 * @author vinay
 */
public class CORSRouteBuilder extends AbstractRouterBuilderImpl {

  private String mAllowedOrigin;
  private Set<String> mAllowedHeaders;
  private String[] mAllowedMethod;
  private int mMaxAgeSeconds;
  private boolean mAllowedCredentials;

  @Override
  public Router getRouter(Router pRouter) {

    CorsHandler crosHdl = CorsHandler.create(getAllowedOrigin());

    Arrays
            .stream(getAllowedMethod())
            .forEach((method) -> {
              crosHdl.allowedMethod(HttpMethod.valueOf(method));
            });

    crosHdl.allowedHeaders(getAllowedHeaders());
    crosHdl.maxAgeSeconds(getMaxAgeSeconds());
    crosHdl.allowCredentials(isAllowedCredentials());

    pRouter.route().handler(crosHdl);

    return pRouter;
  }

  public String getAllowedOrigin() {
    return mAllowedOrigin;
  }

  public void setAllowedOrigin(String pAllowedOrigin) {
    this.mAllowedOrigin = pAllowedOrigin;
  }

  public boolean isAllowedCredentials() {
    return mAllowedCredentials;
  }

  public void setAllowedCredentials(boolean pAllowedCredentials) {
    this.mAllowedCredentials = pAllowedCredentials;
  }

  public Set<String> getAllowedHeaders() {
    return mAllowedHeaders;
  }

  public void setAllowedHeaders(Set<String> pAllowedHeaders) {
    this.mAllowedHeaders = pAllowedHeaders;
  }

  public int getMaxAgeSeconds() {
    return mMaxAgeSeconds;
  }

  public void setMaxAgeSeconds(int pMaxAgeSeconds) {
    this.mMaxAgeSeconds = pMaxAgeSeconds;
  }

  public String[] getAllowedMethod() {
    return mAllowedMethod;
  }

  public void setAllowedMethod(String[] pAllowedMethod) {
    this.mAllowedMethod = pAllowedMethod;
  }

}
