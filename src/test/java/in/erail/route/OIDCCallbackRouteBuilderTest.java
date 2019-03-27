package in.erail.route;

import com.google.common.net.HttpHeaders;
import in.erail.glue.Glue;
import in.erail.server.Server;
import io.vertx.ext.auth.DummyAuthProvider;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
public class OIDCCallbackRouteBuilderTest {

  @Test
  public void testHandle(VertxTestContext testContext) {
    Server server = Glue.instance().resolve("/in/erail/server/Server");
    OIDCCallbackRouteBuilder oid = Glue.instance().resolve("/in/erail/route/OIDCCallbackRouteBuilder");
    oid.setAuthProvider(new AuthProvider(new DummyAuthProvider()));

    WebClient
            .create(server.getVertx())
            .get(server.getHttpServerOptions().getPort(), server.getHttpServerOptions().getHost(), "/callback")
            .addQueryParam("code", "nothing")
            .rxSend()
            .doOnSuccess(req -> assertEquals(302, req.statusCode()))
            .doOnSuccess(req -> assertTrue(req.getHeader(HttpHeaders.LOCATION).endsWith("?login=success")))
            .subscribe(req -> testContext.completeNow(), err -> testContext.failNow(err));
  }
}
