package in.erail.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import in.erail.glue.annotation.StartService;
import io.vertx.reactivex.core.Vertx;

/**
 *
 * @author vinay
 */
public class Registry {
  private Vertx mVertx;
  private MetricRegistry mMetricRegistry;
  private String mRegistryName;
  
  @StartService
  public void start(){
    mMetricRegistry = SharedMetricRegistries.getOrCreate(getRegistryName());
  }

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  public MetricRegistry getMetricRegistry() {
    return mMetricRegistry;
  }

  public String getRegistryName() {
    return mRegistryName;
  }

  public void setRegistryName(String pRegistryName) {
    this.mRegistryName = pRegistryName;
  }
  
}
