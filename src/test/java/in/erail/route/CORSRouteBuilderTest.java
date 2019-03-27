package in.erail.route;

import com.google.common.net.HttpHeaders;
import in.erail.server.Server;
import static org.junit.jupiter.api.Assertions.*;

import in.erail.glue.Glue;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
public class CORSRouteBuilderTest {

  @Test
  public void testProcess(VertxTestContext testContext) {

    Server server = Glue.instance().resolve("/in/erail/server/Server");

    WebClient
            .create(server.getVertx())
            .request(HttpMethod.OPTIONS, server.getHttpServerOptions().getPort(), server.getHttpServerOptions().getHost(), "/v1/broadcast/testTopic")
            .putHeader("content-type", "application/json")
            .putHeader(HttpHeaders.ORIGIN, "https://test.com")
            .putHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
            .putHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "cache-control,content-type,postman-token")
            .rxSend()
            .doOnSuccess(req -> assertEquals(200, req.statusCode()))
            .doOnSuccess((req) -> {
              assertEquals(req.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS.toString()), "DELETE,GET,HEAD,OPTIONS,PATCH,POST,PUT");
              assertEquals(req.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString()), "*");
              assertEquals(req.getHeader(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE.toString()), "3600");
              assertEquals(req.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString()), "X-POST");
            })
            .subscribe(req -> testContext.completeNow(), err -> testContext.failNow(err));
  }

}
