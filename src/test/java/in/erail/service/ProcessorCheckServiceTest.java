package in.erail.service;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
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

/**
 *
 * @author vinay
 */
@RunWith(VertxUnitRunner.class)
public class ProcessorCheckServiceTest {

  @Rule
  public Timeout rule = Timeout.seconds(2000);

  @SuppressWarnings("deprecation")
  @Test
  public void testProcess(TestContext context) {

    Async async = context.async();

    Server server = Glue.instance().resolve("/in/erail/server/Server");

    //Broadcast Request
    String json = new JsonObject().put("data", "testdata").toString();
    server
            .getVertx()
            .createHttpClient()
            .get(server.getHttpServerOptions().getPort(), server.getHttpServerOptions().getHost(), "/v1/processcheck")
            .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString())
            .putHeader(HttpHeaders.ORIGIN, "https://test.com")
            .putHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(json.length()))
            .putHeader(HttpHeaders.AUTHORIZATION, TestConstants.ACCESS_TOKEN)
            .handler(response -> {
              context.assertEquals(response.statusCode(), 200, response.statusMessage());
              context.assertEquals(response.getHeader("ProcessorHeader"), "Header1Header2");
              context.assertTrue(MediaType.parse(response.getHeader(HttpHeaders.CONTENT_TYPE)).equals(MediaType.PLAIN_TEXT_UTF_8));
              response.bodyHandler((event) -> {
                context.assertEquals(event.toString(), "Subject1Subject2");
                async.countDown();
              });
            })
            .write(json)
            .end();

  }

}
