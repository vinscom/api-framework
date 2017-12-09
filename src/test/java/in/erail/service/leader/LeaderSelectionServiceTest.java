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
import io.reactivex.schedulers.Schedulers;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.Session;

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

    Async async = context.async(2);

    LeaderSelectionService service = Glue.instance().<LeaderSelectionService>resolve("/in/erail/service/leader/LeaderSelectionService");
    Session session = service.getSessionStore().createSession(1000000);
    service.getSessionStore().rxPut(session).blockingGet();

    JsonObject regsiterMsg = new JsonObject();
    regsiterMsg.put("type", BridgeEventType.REGISTER.toString());
    regsiterMsg.put("address", "ninja");
    regsiterMsg.put("session", session.id());

    JsonObject unregsiterMsg = new JsonObject();
    unregsiterMsg.put("type", BridgeEventType.UNREGISTER.toString());
    unregsiterMsg.put("address", "ninja");
    unregsiterMsg.put("session", session.id());

    EventBus eb = service.getVertx().eventBus();

    eb
            .<JsonObject>consumer("ninja", (event) -> {
              String leaderId = event.body().getString("leader");
              eb.send(leaderId, new JsonObject());
              service.getVertx().setTimer(2000, (t) -> {
                service
                        .getVertx()
                        .sharedData()
                        .<String, String>getClusterWideMap(service.getLeaderMapName(), (m) -> {
                          m.result().get("ninja", (v) -> {
                            context.assertEquals(leaderId.split("#")[0], v.result());
                            async.countDown();
                            service.getVertx().eventBus().send(service.getBridgeEventUpdateTopicName(), unregsiterMsg);
                            service.getVertx().setTimer(2000, (p) -> {
                              m.result().get("ninja", (v2) -> {
                                Session session3 = service.getSessionStore().rxGet(session.id()).subscribeOn(Schedulers.computation()).blockingGet();
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
