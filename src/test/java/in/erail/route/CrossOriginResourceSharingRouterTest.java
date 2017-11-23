package in.erail.route;

import in.erail.server.Server;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Rule;
import in.erail.glue.Glue;
import io.netty.handler.codec.http.HttpHeaderNames;

/**
 *
 * @author vinay
 */
@RunWith(VertxUnitRunner.class)
public class CrossOriginResourceSharingRouterTest {

  @Rule
  public Timeout rule = Timeout.seconds(2000);

  @Test
  public void testProcess(TestContext context) {

    Async async = context.async();

    Server server = Glue.instance().<Server>resolve("/in/erail/server/Server");

    server
            .getVertx()
            .createHttpClient()
            .options(server.getPort(), server.getHost(), "/v1/broadcast/testTopic")
            .putHeader("content-type", "application/json")
            .handler(response -> {
              context.assertEquals(response.statusCode(), 200, response.statusMessage());
              context.assertEquals(response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString()),"X-POST");
              context.assertEquals(response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS.toString()),"POST");
              context.assertEquals(response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString()),"*");
              context.assertEquals(response.getHeader(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE.toString()),"3600");
              async.complete();
            })
            .end();

  }

}
