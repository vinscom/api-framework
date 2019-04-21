package in.erail.service;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import in.erail.server.Server;

import io.vertx.core.json.JsonObject;
import in.erail.glue.Glue;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
public class BinaryBodyServiceTest {

  @Test
  public void testProcess(VertxTestContext testContext) {

    Server server = Glue.instance().resolve("/in/erail/server/Server");

    //Broadcast Request
    JsonObject json = new JsonObject().put("data", "testdata");

    WebClient
            .create(server.getVertx())
            .post(server.getHttpServerOptions().getPort(), server.getHttpServerOptions().getHost(), "/v1/broadcastv2/testTopic")
            .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString())
            .putHeader(HttpHeaders.ORIGIN, "https://test.com")
            .rxSendJsonObject(json)
            .doOnSuccess(req -> assertEquals(200, req.statusCode()))
            .doOnSuccess(response -> {
              assertEquals(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN), "*");
              assertTrue(MediaType.parse(response.getHeader(HttpHeaders.CONTENT_TYPE)).equals(MediaType.PLAIN_TEXT_UTF_8));
              assertEquals(response.bodyAsString(), "testdata");
            })
            .subscribe(req -> testContext.completeNow(), err -> testContext.failNow(err));

  }

}
