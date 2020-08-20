package in.erail.route;

import in.erail.glue.Glue;
import in.erail.server.Server;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
public class HealthChecksRouteBuilderTest {

  public HealthChecksRouteBuilderTest() {
  }

  @Test
  public void testHealthCheck(VertxTestContext testContext) {
    Server server = Glue.instance().resolve("/in/erail/server/Server");

    WebClient
            .create(server.getVertx())
            .get(server.getHttpServerOptions().getPort(), server.getHttpServerOptions().getHost(), "/internal/health")
            .rxSend()
            .doOnSuccess(req -> assertEquals(204, req.statusCode()))
            .subscribe(req -> testContext.completeNow(), err -> testContext.failNow(err));
  }

}
