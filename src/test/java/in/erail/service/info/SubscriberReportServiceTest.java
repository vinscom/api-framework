package in.erail.service.info;

import com.google.common.net.HttpHeaders;
import in.erail.common.FramworkConstants;
import in.erail.glue.Glue;
import in.erail.server.Server;
import in.erail.test.TestConstants;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventHandler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;

/**
 *
 * @author vinay
 */
@RunWith(VertxUnitRunner.class)
public class SubscriberReportServiceTest {

  @Rule
  public Timeout rule = Timeout.seconds(2000);

  @Test
  public void testProcess(TestContext context) {

    Async async = context.async();

    BridgeEventHandler eventHandler = Glue.instance().<BridgeEventHandler>resolve("/io/vertx/ext/web/handler/sockjs/BridgeEventHandler");
    Server server = Glue.instance().<Server>resolve("/in/erail/server/Server");

    BridgeEvent bEvt = mock(BridgeEvent.class);
    doNothing().when(bEvt).complete();
    doNothing().when(bEvt).complete(true);

    //Add to cluster map
    eventHandler.handleRegister("test.topic", bEvt);
    eventHandler.handleRegister("test.topic", bEvt);

    server
            .getVertx()
            .createHttpClient()
            .get(server.getPort(), server.getHost(), "/v1/debug/topic/all")
            .putHeader("content-type", "application/json")
            .handler(response -> {
              context.assertEquals(response.statusCode(), 200, response.statusMessage());
              response.bodyHandler((event) -> {
                JsonObject item = event.toJsonArray().getJsonObject(0);
                String topic = item.getString("topic");
                Long count = item.getLong("count");
                context.assertEquals("test.topic", topic);
                context.assertEquals(2l, count);
                async.countDown();
              });
            })
            .end();

  }

}
