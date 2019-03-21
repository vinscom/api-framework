package in.erail.service;

import in.erail.glue.Glue;
import io.reactivex.Observable;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
public class SingletonServiceImplTest {

  @Test
  public void testStart(VertxTestContext testContext) throws InterruptedException, ExecutionException {

    Vertx vertx = Glue.instance().resolve("/io/vertx/core/Vertx");
    ClusterManager cm = Glue.instance().resolve("/io/vertx/spi/cluster/ClusterManager");

    Checkpoint firstCP = testContext.checkpoint();
    Checkpoint secondCP = testContext.checkpoint();

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
                                .flatMapMaybe((m) -> {
                                  return m.rxGet("DummySingletonService");
                                })
                                .doOnSuccess((nodeId) -> {
                                  assertNotEquals(cm.getNodeID(), nodeId);  //Validate after starting this service. Control is still with other node
                                  firstCP.flag();
                                })
                                .subscribe((nodeId) -> {
                                  service.nodeLeft("NodeDownID"); //Trigger remote node leave
                                  Observable
                                          .timer(100, TimeUnit.MILLISECONDS)
                                          .subscribe((p) -> {
                                            vertx
                                                    .sharedData()
                                                    .<String, String>rxGetClusterWideMap("__in.erail.services")
                                                    .flatMapMaybe(m2 -> {
                                                      return m2.rxGet("DummySingletonService");
                                                    })
                                                    .subscribe((updatedNodeId) -> {
                                                      assertEquals(cm.getNodeID(), updatedNodeId);
                                                      assertEquals(service.getRecorder().size(), 1);
                                                      secondCP.flag();
                                                    });
                                          });
                                });
                      });
            });

  }

}
