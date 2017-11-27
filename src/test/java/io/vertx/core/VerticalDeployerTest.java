package io.vertx.core;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Rule;
import in.erail.glue.Glue;

/**
 *
 * @author vinay
 */
@RunWith(VertxUnitRunner.class)
public class VerticalDeployerTest {

  @Rule
  public Timeout rule = Timeout.seconds(2000);

  @Test
  public void testJSDeployment(TestContext context) {

    Async async = context.async(4);
    io.vertx.reactivex.core.Vertx vertx = Glue.instance().<VertxInstance>resolve("/io/vertx/core/VertxInstance").getVertx();

    //Reply from vertical
    vertx.eventBus().<JsonObject>consumer("vertical.test.js", (event) -> {
      async.countDown();
    });
    
    vertx.eventBus().<JsonObject>consumer("vertical.test.java", (event) -> {
      async.countDown();
    });

    //Deployment status
    vertx.eventBus().<JsonObject>consumer("vertical.deploy.status", (event) -> {
      Boolean status = event.body().getBoolean("success");
      context.assertTrue(status);
      async.countDown();
    });

    VerticalDeployer deployer = Glue.instance().<VerticalDeployer>resolve("/io/vertx/core/VerticalDeployer");

  }

}
