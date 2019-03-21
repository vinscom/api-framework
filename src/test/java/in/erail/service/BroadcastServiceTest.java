package in.erail.service;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import in.erail.server.Server;
import in.erail.test.TestConstants;

import io.vertx.core.json.JsonObject;

import in.erail.glue.Glue;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.client.WebClient;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
public class BroadcastServiceTest {

  @Test
  public void testProcess(VertxTestContext testContext) {

    Server server = Glue.instance().resolve("/in/erail/server/Server");

    //API Reply
    server
            .getVertx()
            .eventBus()
            .<JsonObject>consumer("testTopic", (event) -> {
              String data = event.body().getString("data");
              assertEquals("testdata", data);
            });

    //Broadcast Request
    JsonObject json = new JsonObject().put("data", "testdata");

    WebClient
            .create(server.getVertx())
            .post(server.getHttpServerOptions().getPort(), server.getHttpServerOptions().getHost(), "/v1/broadcast/testTopic")
            .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString())
            .putHeader(HttpHeaders.ORIGIN, "https://test.com")
            .putHeader(HttpHeaders.AUTHORIZATION, TestConstants.ACCESS_TOKEN)
            .rxSendJsonObject(json)
            .doOnSuccess(req -> assertEquals(200, req.statusCode()))
            .doOnSuccess(req -> {
              assertEquals(req.statusCode(), 200, req.statusMessage());
              assertEquals(req.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN), "*");
              assertTrue(MediaType.parse(req.getHeader(HttpHeaders.CONTENT_TYPE)).equals(MediaType.JSON_UTF_8));
              assertEquals(req.bodyAsString(), TestConstants.Service.Message.successMessage().toString());
            })
            .subscribe(req -> testContext.completeNow(), err -> testContext.failNow(err));

  }

}
