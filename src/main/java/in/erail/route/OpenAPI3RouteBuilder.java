package in.erail.route;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Metered;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static in.erail.common.FrameworkConstants.RoutingContext.Json;
import in.erail.glue.annotation.StartService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import in.erail.service.RESTService;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.Cookie;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

/**
 *
 * @author vinay
 */
public class OpenAPI3RouteBuilder extends AbstractRouterBuilderImpl {

  private static final String AUTHORIZATION_PREFIX = "realm";
  private static final String FAIL_SUFFIX = ".fail";
  private RESTService[] mServices;
  private File mOpenAPI3File;
  private DeliveryOptions mDeliveryOptions;
  private boolean mSecurityEnable = true;
  private HashMap<String, Metered> mMetrics = new HashMap<>();
  private MetricRegistry mMetricRegistry;

  public File getOpenAPI3File() {
    return mOpenAPI3File;
  }

  public void setOpenAPI3File(File pOpenAPI3File) {
    this.mOpenAPI3File = pOpenAPI3File;
  }

  public RESTService[] getServices() {
    return mServices;
  }

  public void setServices(RESTService[] pServices) {
    this.mServices = pServices;
  }

  @StartService
  public void start() {

    Arrays
            .stream(getServices())
            .forEach((service) -> {
              getMetrics()
                      .put(service.getServiceUniqueId(),
                              getMetricRegistry().timer("api.framework.service." + service.getServiceUniqueId()));
              getMetrics()
                      .put(service.getServiceUniqueId() + FAIL_SUFFIX,
                              getMetricRegistry().meter("api.framework.service." + service.getServiceUniqueId() + FAIL_SUFFIX));
            });

  }

  public void process(RoutingContext pRequestContext, String pServiceUniqueId) {

    Timer.Context timerCtx = ((Timer) getMetrics().get(pServiceUniqueId)).time();

    getVertx()
            .eventBus()
            .send(pServiceUniqueId,
                    serialiseRoutingContext(pRequestContext),
                    getDeliveryOptions(),
                    (reply) -> {
                      if (reply.succeeded()) {
                        JsonObject response = (JsonObject) reply.result().body();
                        buildResponseFromReply(response, pRequestContext).end();
                      } else {
                        ((Meter) getMetrics().get(pServiceUniqueId + FAIL_SUFFIX)).mark();
                        getLog().error(() -> "Error in reply:" + reply.cause().toString());
                        pRequestContext
                                .response()
                                .setStatusCode(400)
                                .end(reply.cause().toString());
                      }
                      timerCtx.stop();
                    });

  }

  /**
   * In case of post request. Body is sent in binary
   *
   * @param pContext Routing Context
   * @return JsonObject representing RoutingContext
   */
  public JsonObject serialiseRoutingContext(RoutingContext pContext) {

    JsonObject result = new JsonObject();

    if (pContext.request().method() == HttpMethod.POST) {
      result.put(Json.BODY, pContext.getBody().getDelegate().getBytes());
    } else {
      result.put(Json.BODY, new byte[]{});
    }

    JsonObject headers = new JsonObject(convertMultiMapIntoMap(pContext.request().headers()));
    result.put(Json.HEADERS, headers);

    JsonObject query = new JsonObject(convertMultiMapIntoMap(pContext.queryParams()));
    result.put(Json.QUERY_STRING_PARAM, query);

    JsonObject params = new JsonObject(convertMultiMapIntoMap(pContext.request().params()));
    result.put(Json.PATH_PARAM, params);

    getLog().debug(() -> "Context to JSON:" + result.toString());

    return result;
  }

  /**
   * All response content is written in binary. If Content type is not provided then application/octet-stream content type is set.
   *
   * @param pReplyResponse Service Body
   * @param pContext Routing Context
   * @return HttpServerResponse
   */
  public HttpServerResponse buildResponseFromReply(JsonObject pReplyResponse, RoutingContext pContext) {

    JsonObject headers = pReplyResponse.getJsonObject(Json.HEADERS, new JsonObject());
    String statusCode = pReplyResponse.getString(Json.STATUS_CODE, HttpResponseStatus.OK.codeAsText().toString());

    if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
      headers.put(HttpHeaders.CONTENT_TYPE, MediaType.OCTET_STREAM.toString());
    }

    headers
            .fieldNames()
            .stream()
            .forEach((field) -> {
              pContext.response().putHeader(field, headers.getString(field, ""));
            });

    pContext.response().setStatusCode(HttpResponseStatus.parseLine(statusCode).code());

    Optional<JsonArray> cookies = Optional.ofNullable(pReplyResponse.getJsonArray(Json.COOKIES));

    cookies.ifPresent((cooky) -> {
      for (Iterator<Object> iterator = cooky.iterator(); iterator.hasNext();) {
        JsonObject next = (JsonObject) iterator.next();
        Optional cookieName = Optional.ofNullable(next.getString(Json.Cookie.NAME));
        if (cookieName.isPresent()) {
          Cookie c = Cookie.cookie((String) cookieName.get(), "");
          Optional.ofNullable(next.getString(Json.Cookie.VALUE)).ifPresent(t -> c.setValue(t));
          Optional.ofNullable(next.getString(Json.Cookie.PATH)).ifPresent(t -> c.setPath(t));
          Optional.ofNullable(next.getDouble(Json.Cookie.MAX_AGE)).ifPresent(t -> c.setMaxAge(t.longValue()));
          Optional.ofNullable(next.getString(Json.Cookie.DOMAIN)).ifPresent(t -> c.setDomain(t));
          Optional.ofNullable(next.getBoolean(Json.Cookie.SECURE)).ifPresent(t -> c.setSecure(t));
          Optional.ofNullable(next.getBoolean(Json.Cookie.HTTP_ONLY)).ifPresent(t -> c.setHttpOnly(t));
          pContext.addCookie(c);
        }
      }
    });

    Optional<byte[]> body;

    try {
      body = Optional.ofNullable(pReplyResponse.getBinary(Json.BODY));
    } catch (IllegalArgumentException e) {
      getLog().error(() -> "Could not get message body as binary. Please check if service is sending body in binary." + pContext.request().absoluteURI() + ":" + e.toString());
      body = Optional.empty();
    }

    body.ifPresent((t) -> {
      pContext.response().putHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), Integer.toString(t.length));
      pContext.response().write(Buffer.newInstance(io.vertx.core.buffer.Buffer.buffer(t)));
    });

    return pContext.response();
  }

  public Map<String, Object> convertMultiMapIntoMap(MultiMap pMultiMap) {
    return pMultiMap
            .getDelegate()
            .entries()
            .stream()
            .collect(Collectors.toMap((t) -> t.getKey(), (t) -> t.getValue(), (a, b) -> a));
  }

  public DeliveryOptions getDeliveryOptions() {
    return mDeliveryOptions;
  }

  public void setDeliveryOptions(DeliveryOptions pDeliveryOptions) {
    this.mDeliveryOptions = pDeliveryOptions;
  }

  @Override
  public Router getRouter(Router pRouter) {

    OpenAPI3RouterFactory apiFactory = OpenAPI3RouterFactory
            .rxCreateRouterFactoryFromFile(getVertx(), getOpenAPI3File().getAbsolutePath())
            .blockingGet();

    Optional
            .ofNullable(getServices())
            .ifPresent(t -> {
              Arrays
                      .asList(t)
                      .stream()
                      .forEach((service) -> {
                        apiFactory.addHandlerByOperationId(service.getOperationId(), (routingContext) -> {
                          if (isSecurityEnable()) {

                            if (routingContext.user() == null) {
                              routingContext.fail(401);
                              return;
                            }

                            routingContext.user().isAuthorized(AUTHORIZATION_PREFIX + ":" + service.getOperationId(), (event) -> {
                              boolean authSuccess = event.succeeded() ? event.result() : false;
                              if (authSuccess) {
                                process(routingContext, service.getServiceUniqueId());
                              } else {
                                routingContext.fail(401);
                              }
                            });
                          } else {
                            getLog().warn("Security disabled for " + service.getServiceUniqueId());
                            process(routingContext, service.getServiceUniqueId());
                          }
                        });
                        
                        apiFactory.addFailureHandlerByOperationId(service.getOperationId(),(routingContext) -> {
                            routingContext
                                .response()
                                .setStatusCode(400)
                                .end(routingContext.failure().toString());
                        });
                      });
            });

    return apiFactory.getRouter();
  }

  public boolean isSecurityEnable() {
    return mSecurityEnable;
  }

  public void setSecurityEnable(boolean pSecurityEnable) {
    this.mSecurityEnable = pSecurityEnable;
  }

  public void setOpenAPI3JSON(JsonObject pOpenAPI3JSON) {
    try {
      File f = File.createTempFile("openapi3json", null);
      getVertx().getDelegate().fileSystem().writeFileBlocking(f.getAbsolutePath(), pOpenAPI3JSON.toBuffer());
      setOpenAPI3File(f);
    } catch (IOException ex) {
      getLog().error(ex);
    }
  }

  public HashMap<String, Metered> getMetrics() {
    return mMetrics;
  }

  public void setMetrics(HashMap<String, Metered> pMetrics) {
    this.mMetrics = pMetrics;
  }

  public MetricRegistry getMetricRegistry() {
    return mMetricRegistry;
  }

  public void setMetricRegistry(MetricRegistry pMetricRegistry) {
    this.mMetricRegistry = pMetricRegistry;
  }

}
