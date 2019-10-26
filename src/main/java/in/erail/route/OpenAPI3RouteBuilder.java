package in.erail.route;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Metered;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import in.erail.common.FrameworkConstants.RoutingContext.Json;
import in.erail.glue.annotation.StartService;
import in.erail.model.RequestEvent;
import in.erail.model.ResponseEvent;
import in.erail.service.RESTService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.reactivex.Observable;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.Cookie;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;

/**
 *
 * @author vinay
 */
public class OpenAPI3RouteBuilder extends AbstractRouterBuilderImpl {

  private static final String HEADER_X_REQUEST_ID = "X-Request-ID";
  private static final String FAIL_SUFFIX = ".fail";
  private RESTService[] mServices;
  private File mOpenAPI3File;
  private DeliveryOptions mDeliveryOptions;
  private boolean mSecurityEnable = true;
  private HashMap<String, Metered> mMetrics = new HashMap<>();
  private MetricRegistry mMetricRegistry;
  private String mRequestIdHeaderName = HEADER_X_REQUEST_ID;

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

    String requestId = pRequestContext.request().getHeader(getRequestIdHeaderName());

    if (Strings.isNullOrEmpty(requestId)) {
      requestId = UUID.randomUUID().toString();
      pRequestContext.request().headers().add(getRequestIdHeaderName(), UUID.randomUUID().toString());
    }

    pRequestContext.response().putHeader(getRequestIdHeaderName(), requestId);

    getVertx()
            .eventBus()
            .request(pServiceUniqueId,
                    serialiseRoutingContext(pRequestContext),
                    getDeliveryOptions(),
                    (reply) -> {
                      if (reply.succeeded()) {
                        JsonObject response = (JsonObject) reply.result().body();
                        HttpServerResponse resp = buildResponseFromReply(response, pRequestContext);
                        resp.end();
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

    RequestEvent request = new RequestEvent();
    request.setHttpMethod(pContext.request().method());

    if (request.getHttpMethod() == HttpMethod.POST
            || request.getHttpMethod() == HttpMethod.PUT
            || request.getHttpMethod() == HttpMethod.PATCH) {
      request.setBody(pContext.getBody().getDelegate().getBytes());
    }

    request.setHeaders(convertMultiMapIntoMap(pContext.request().headers()));
    request.setQueryStringParameters(convertMultiMapIntoMap(pContext.queryParams()));
    request.setPathParameters(convertMultiMapIntoMap(pContext.request().params()));

    JsonObject principal = Optional
            .ofNullable(pContext.user())
            .flatMap((t) -> Optional.ofNullable(t.principal()))
            .orElse(new JsonObject());

    request.setPrincipal(principal.getMap());

    JsonObject result = JsonObject.mapFrom(request);

    getLog().debug(() -> "Context to JSON:" + result.toString());

    return result;
  }

  /**
   * All response content is written in binary. If Content type is not provided
   * then application/octet-stream content type is set.
   *
   * @param pReplyResponse Service Body
   * @param pContext Routing Context
   * @return HttpServerResponse
   */
  public HttpServerResponse buildResponseFromReply(JsonObject pReplyResponse, RoutingContext pContext) {

    ResponseEvent response = pReplyResponse.mapTo(ResponseEvent.class);

    Optional<String> contentType = Optional.ofNullable(response.headerValue(HttpHeaders.CONTENT_TYPE));

    if (!contentType.isPresent()) {
      response.setMediaType(MediaType.OCTET_STREAM);
    }

    response
            .getHeaders()
            .entrySet()
            .stream()
            .forEach((kv) -> {
              pContext.response().putHeader(kv.getKey(), kv.getValue());
            });

    pContext.response().setStatusCode(response.getStatusCode());

    @SuppressWarnings("unchecked")
    Map<String, String>[] cookies = Optional.ofNullable(response.getCookies()).orElse(new Map[0]);

    Arrays
            .stream(cookies)
            .map((t) -> {
              Optional<String> cookieName = Optional.ofNullable(t.get(Json.Cookie.NAME));
              if (cookieName.isPresent()) {
                Cookie c = Cookie.cookie((String) cookieName.get(), "");
                Optional.ofNullable(t.get(Json.Cookie.VALUE)).ifPresent(v -> c.setValue(v));
                Optional.ofNullable(t.get(Json.Cookie.PATH)).ifPresent(v -> c.setPath(v));
                Optional.ofNullable(t.get(Json.Cookie.MAX_AGE)).ifPresent(v -> c.setMaxAge(Long.parseLong(v)));
                Optional.ofNullable(t.get(Json.Cookie.DOMAIN)).ifPresent(v -> c.setDomain(v));
                Optional.ofNullable(t.get(Json.Cookie.SECURE)).ifPresent(v -> c.setSecure(Boolean.parseBoolean(v)));
                Optional.ofNullable(t.get(Json.Cookie.HTTP_ONLY)).ifPresent(v -> c.setHttpOnly(Boolean.parseBoolean(v)));
                return Optional.of(c);
              }
              return Optional.<Cookie>empty();
            })
            .filter(t -> t.isPresent())
            .forEach(t -> pContext.addCookie(t.get()));

    Optional<byte[]> body = Optional
            .ofNullable(response.getBody());

    body.ifPresent((t) -> {
      pContext.response().putHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), Integer.toString(t.length));
      pContext.response().write(Buffer.buffer(t));
    });

    return pContext.response();
  }

  public Map<String, String> convertMultiMapIntoMap(MultiMap pMultiMap) {
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
            .rxCreate(getVertx(), getOpenAPI3File().getAbsolutePath())
            .blockingGet();

    Optional
            .ofNullable(getServices())
            .ifPresent(t -> {
              Arrays
                      .asList(t)
                      .stream()
                      .forEach((service) -> {
                        getLog().debug(() -> "Adding OpenAPI service handle:" + service.getOperationId());
                        apiFactory.addHandlerByOperationId(service.getOperationId(), (routingContext) -> {
                          if (isSecurityEnable() && service.isSecure()) {

                            if (routingContext.user() == null) {
                              getLog().error(() -> "Security on this operation is enabled. But, no user found. Please check, if you are passing correct auth credentials:" + service.getOperationId());
                              getLog().trace(() -> dumpRequest(routingContext.request()));
                              routingContext.fail(401);
                              return;
                            }

                            Optional<String[]> authorities = Optional.ofNullable(service.getAuthority());

                            if (authorities.isPresent()) {
                              Observable
                                      .fromArray(authorities.get())
                                      .flatMapSingle(a -> routingContext.user().rxIsAuthorized(a))
                                      .filter(a -> a)
                                      .firstElement()
                                      .subscribe((success) -> {
                                        getLog().trace(() -> "Request is authorized. Processing Message:" + service.getOperationId());
                                        getLog().trace(() -> dumpRequest(routingContext.request()));
                                        process(routingContext, service.getServiceUniqueId());
                                      }, (err) -> {
                                        getLog().error(() -> "Error processing serive message", err);
                                        routingContext.fail(err);
                                      }, () -> {
                                        getLog().warn(() -> "You are not authorized to access service:" + service.getOperationId());
                                        routingContext.fail(401);
                                      });
                            } else {
                              getLog().error(() -> "Service marked as secure, but, no authority defined at service level:" + service.getOperationId());
                              routingContext.fail(401);
                            }

                          } else {
                            getLog().warn("Security disabled for " + service.getServiceUniqueId());
                            getLog().trace(() -> dumpRequest(routingContext.request()));
                            process(routingContext, service.getServiceUniqueId());
                          }
                        });

                        apiFactory.addFailureHandlerByOperationId(service.getOperationId(), (routingContext) -> {
                          int respStatusCode = routingContext.statusCode();
                          if (respStatusCode == -1) {
                            respStatusCode = 400;
                          }

                          getLog().debug(() -> "API Failure Handle called:" + service.getOperationId() + ":" + generateErrorResponse(routingContext));
                          getLog().trace(() -> dumpRequest(routingContext.request()));

                          routingContext
                                  .response()
                                  .setStatusCode(respStatusCode)
                                  .end(generateErrorResponse(routingContext));
                        });
                      });
            });

    return apiFactory.getRouter();
  }

  protected String dumpRequest(HttpServerRequest pRequest) {
    return MoreObjects
            .toStringHelper(pRequest)
            .add("RequestPath", pRequest.absoluteURI())
            .add("Headers", Joiner.on(",").join(pRequest.headers().entries().stream().map((e) -> e.getKey() + ":" + e.getValue()).collect(Collectors.toList())))
            .add("QueryParam", pRequest.query())
            .toString();
  }

  protected String generateErrorResponse(RoutingContext pContext) {
    return Optional
            .ofNullable(pContext.failure())
            .map(error -> ExceptionUtils.getMessage(error))
            .orElse(pContext.getBodyAsString());
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
      Files.write(pOpenAPI3JSON.toBuffer().getBytes(), f);
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

  public String getRequestIdHeaderName() {
    return mRequestIdHeaderName;
  }

  public void setRequestIdHeaderName(String pRequestIdHeaderName) {
    this.mRequestIdHeaderName = pRequestIdHeaderName;
  }

}
