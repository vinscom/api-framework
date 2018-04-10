package in.erail.service;

import in.erail.glue.Glue;
import io.reactivex.Observable;
import io.vertx.core.VertxInstance;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 *
 * @author vinay
 */
@RunWith(VertxUnitRunner.class)
public class SingletonServiceImplTest {

  @Rule
  public Timeout rule = Timeout.seconds(2000);

  @Test
  public void testStart(TestContext context) throws InterruptedException, ExecutionException {

    Async async = context.async(2);

    Vertx vertx = Glue.instance().resolve("/io/vertx/core/Vertx");
    ClusterManager cm = Glue.instance().resolve("/io/vertx/spi/cluster/ClusterManager");

    vertx
            .sharedData()
            .<String, String>rxGetClusterWideMap("__in.erail.services")
            .flatMapCompletable((m) -> m.rxPut("DummySingletonService", "NodeDownID")) //Service is running on some other node
            .subscribe(() -> {
              DummySingletonService service = Glue.instance().resolve("/in/erail/service/DummySingletonService");
              Observable
                      .timer(100, TimeUnit.MILLISECONDS)
                      .subscribe((t) -> {
                        vertx
                                .sharedData()
                                .<String, String>rxGetClusterWideMap("__in.erail.services")
                                .flatMap((m) -> {
                                  return m.rxGet("DummySingletonService");
                                })
                                .doOnSuccess((nodeId) -> {
                                  context.assertNotEquals(cm.getNodeID(), nodeId);  //Validate after starting this service. Control is still with other node
                                  async.countDown();
                                })
                                .subscribe((nodeId) -> {
                                  service.nodeLeft("NodeDownID"); //Trigger remote node leave
                                  Observable
                                          .timer(100, TimeUnit.MILLISECONDS)
                                          .subscribe((p) -> {
                                            vertx
                                                    .sharedData()
                                                    .<String, String>rxGetClusterWideMap("__in.erail.services")
                                                    .flatMap(m2 -> {
                                                      return m2.rxGet("DummySingletonService");
                                                    })
                                                    .subscribe((updatedNodeId) -> {
                                                      context.assertEquals(cm.getNodeID(), updatedNodeId);
                                                      context.assertEquals(service.getRecorder().size(), 1);
                                                      async.countDown();
                                                    });
                                          });
                                });
                      });
            });

  }

}
