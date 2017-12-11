package in.erail.service.leader;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Rule;
import in.erail.glue.Glue;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.reactivex.core.eventbus.EventBus;

/**
 *
 * @author vinay
 */
@RunWith(VertxUnitRunner.class)
public class LeaderSelectionServiceTest {

  @Rule
  public Timeout rule = Timeout.seconds(2000);

  @Test
  public void testRegister(TestContext context) {

    Async async = context.async(3);

    LeaderSelectionService service = Glue.instance().<LeaderSelectionService>resolve("/in/erail/service/leader/LeaderSelectionService");

    String session = "FAKE_SESSION";

    JsonObject regsiterMsg = new JsonObject();
    regsiterMsg.put("type", BridgeEventType.REGISTER.toString());
    regsiterMsg.put("address", "ninja-live");
    regsiterMsg.put("session", session);

    JsonObject unregsiterMsg = new JsonObject();
    unregsiterMsg.put("type", BridgeEventType.UNREGISTER.toString());
    unregsiterMsg.put("address", "ninja-live");
    unregsiterMsg.put("session", session);

    EventBus eb = service.getVertx().eventBus();

    eb
            .<JsonObject>consumer("ninja-live", (event) -> {
              String leaderId = event.body().getString("leader");
              eb.send(leaderId, new JsonObject(), (e) -> {
                if (e.succeeded()) {
                  async.countDown();
                }
              });
              service.getVertx().setTimer(100, (t) -> {
                service
                        .getVertx()
                        .sharedData()
                        .<String, String>getClusterWideMap(service.getLeaderMapName(), (m) -> {
                          m.result().get("ninja-live", (v) -> {
                            context.assertEquals(leaderId.split("#")[0], v.result());
                            async.countDown();
                            service.getVertx().eventBus().send(service.getBridgeEventUpdateTopicName(), unregsiterMsg);
                            service.getVertx().setTimer(100, (p) -> {
                              m.result().get("ninja-live", (v2) -> {
                                context.assertNull(v2.result());
                                async.countDown();
                              });
                            });
                          });
                        });
              });
            });

    service.getVertx().eventBus().send(service.getBridgeEventUpdateTopicName(), regsiterMsg);

  }

}
