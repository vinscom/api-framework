package in.erail.route;

import com.google.common.net.HttpHeaders;
import in.erail.glue.Glue;
import in.erail.glue.component.ServiceMap;
import in.erail.server.Server;
import io.vertx.core.Handler;
import io.vertx.ext.auth.DummyAuthProvider;
import io.vertx.ext.healthchecks.Status;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.Router;
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
