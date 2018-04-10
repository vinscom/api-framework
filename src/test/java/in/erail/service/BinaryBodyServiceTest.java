package in.erail.service;

import com.google.common.net.HttpHeaders;
import in.erail.server.Server;
import in.erail.test.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.json.JsonObject;
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
public class BinaryBodyServiceTest {

  @Rule
  public Timeout rule = Timeout.seconds(2000);

  @Test
  public void testProcess(TestContext context) {

    Async async = context.async();

    Server server = Glue.instance().resolve("/in/erail/server/Server");

    //Broadcast Request
    String json = new JsonObject().put("data", "testdata").toString();
    server
            .getVertx()
            .createHttpClient()
            .post(server.getHttpServerOptions().getPort(), server.getHttpServerOptions().getHost(), "/v1/broadcastv2/testTopic")
            .putHeader("content-type", "application/json")
            .putHeader(HttpHeaders.ORIGIN, "https://test.com")
            .putHeader("content-length", Integer.toString(json.length()))
            .putHeader(HttpHeaders.AUTHORIZATION, TestConstants.ACCESS_TOKEN)
            .handler(response -> {
              context.assertEquals(response.statusCode(), 200, response.statusMessage());
              context.assertEquals(response.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString()),"*");
              context.assertEquals(response.getHeader(HttpHeaderNames.CONTENT_TYPE.toString()),"application/json; charset=utf-8");
              response.bodyHandler((event) -> {
                context.assertEquals(event.toString(), "19");
                async.countDown();
              });
            })
            .write(json)
            .end();

  }

}
