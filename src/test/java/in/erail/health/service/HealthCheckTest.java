package in.erail.health.service;

import in.erail.glue.Glue;
import in.erail.server.Server;
import io.vertx.core.json.JsonArray;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class HealthCheckTest {

  @Test
  public void testGetRequest(VertxTestContext testContext) {

    Server server = Glue.instance().<Server>resolve("/in/erail/server/Server");

    WebClient
            .create(server.getVertx())
            .get(server.getHttpServerOptions().getPort(), server.getHttpServerOptions().getHost(), "/v1/internal/healthcheck")
            .rxSend()
            .doOnSuccess(response -> assertEquals(response.statusCode(), 200, response.statusMessage()))
            .doOnSuccess(response -> {
              JsonArray data = response.bodyAsJsonArray();
              assertEquals(1, data.size());
            })
            .subscribe(t -> testContext.completeNow(), err -> testContext.failNow(err));
  }

}
