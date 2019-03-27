package io.vertx.core;

import io.vertx.core.json.JsonObject;
import in.erail.glue.Glue;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
public class VerticalDeployerTest {

  @Test
  public void testVerticalDeployment(VertxTestContext testContext) {

    io.vertx.reactivex.core.Vertx vertx = Glue.instance().resolve("/io/vertx/core/Vertx");
    Checkpoint checkpoint = testContext.laxCheckpoint();
    Checkpoint checkpoint2 = testContext.laxCheckpoint();
    Checkpoint checkpoint3 = testContext.laxCheckpoint();

    //Reply from vertical
    vertx.eventBus().<JsonObject>consumer("vertical.test.js", (event) -> {
      assertEquals("in/erail/vertical/js/TestVerticalService.js", event.body());
      checkpoint.flag();
    });

    vertx.eventBus().<JsonObject>consumer("vertical.test.java", (event) -> {
      checkpoint2.flag();
    });

    //Deployment status
    vertx.eventBus().<JsonObject>consumer("vertical.deploy.status", (event) -> {
      Boolean status = event.body().getBoolean("success");
      assertTrue(status);
      checkpoint3.flag();
    });

    Glue.instance().<VerticalDeployer>resolve("/io/vertx/core/VerticalDeployer");

  }

}
