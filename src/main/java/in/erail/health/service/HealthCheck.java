package in.erail.health.service;

import com.google.common.net.MediaType;
import in.erail.health.HealthStatus;
import in.erail.model.Event;
import in.erail.service.RESTServiceImpl;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.vertx.core.json.JsonArray;

/**
 *
 * @author vinay
 */
public class HealthCheck extends RESTServiceImpl {

  private HealthStatus[] mHealthCheck = new HealthStatus[0];

  @Override
  public MaybeSource<Event> process(Maybe<Event> pEvent) {

    return Observable
            .fromArray(getHealthCheck())
            .reduce(new JsonArray(), (a, v) -> a.add(v.healthStatus()))
            .map(a -> a.encodePrettily())
            .zipWith(pEvent.toSingle(), (a, e) -> {
              e.getResponse().setBody(a.getBytes()).setMediaType(MediaType.JSON_UTF_8);
              return e;
            })
            .toMaybe();
  }

  public HealthStatus[] getHealthCheck() {
    return mHealthCheck;
  }

  public void setHealthCheck(HealthStatus[] pHealthCheck) {
    this.mHealthCheck = pHealthCheck;
  }

}
