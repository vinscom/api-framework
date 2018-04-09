package in.erail.route;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import in.erail.common.FrameworkConstants;
import in.erail.glue.annotation.StartService;
import in.erail.glue.component.ServiceArray;
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
import java.util.HashMap;

/**
 *
 * @author vinay
 */
public class OpenAPI3RouteBuilder extends AbstractRouterBuilderImpl {

  private static final String AUTHORIZATION_PREFIX = "realm";
  private ServiceArray mServices;
  private File mOpenAPI3File;
  private DeliveryOptions mDeliveryOptions;
  private boolean mSecurityEnable = true;
  private HashMap<String, Timer> mMetricTimers = new HashMap<>();
  private MetricRegistry mMetricRegistry;

  public File getOpenAPI3File() {
    return mOpenAPI3File;
  }

  public void setOpenAPI3File(File pOpenAPI3File) {
    this.mOpenAPI3File = pOpenAPI3File;
  }

  public ServiceArray getServices() {
    return mServices;
  }

  public void setServices(ServiceArray pServices) {
    this.mServices = pServices;
  }

  @StartService
  public void start() {

    getServices()
            .getServices()
            .stream()
            .forEach((api) -> {
              RESTService service = (RESTService) api;
              getMetricTimers().put(service.getServiceUniqueId(),
                      getMetricRegistry().timer("api.framework.service." + service.getServiceUniqueId())
              );
            });

  }

  public void process(RoutingContext pRequestContext, String pServiceUniqueId) {

    Timer.Context timerCtx = getMetricTimers().get(pServiceUniqueId).time();

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
                        getLog().error(() -> "Error in reply:" + reply.cause().toString());
                        pRequestContext
                                .response()
                                .setStatusCode(400)
                                .end(reply.cause().toString());
                      }
                      timerCtx.stop();
                    });

  }

  public JsonObject serialiseRoutingContext(RoutingContext pContext) {

    JsonObject result = new JsonObject();

    if (pContext.request().method() == HttpMethod.POST) {
      boolean bodyAsJson = pContext.<Boolean>get(FrameworkConstants.RoutingContext.Attribute.BODY_AS_JSON);
      if (bodyAsJson) {
        String mediaTypeHeader = pContext.request().headers().get(HttpHeaders.CONTENT_TYPE);
        MediaType contentType;
        if (Strings.isNullOrEmpty(mediaTypeHeader)) {
          contentType = MediaType.JSON_UTF_8;
        } else {
          contentType = MediaType.parse(mediaTypeHeader);
        }
        if (MediaType.JSON_UTF_8.type().equals(contentType.type()) && MediaType.JSON_UTF_8.subtype().equals(contentType.subtype())) {
          result.put(FrameworkConstants.RoutingContext.Json.BODY, pContext.getBodyAsJson());
        }
      } else {
        result.put(FrameworkConstants.RoutingContext.Json.BODY, pContext.getBody().getDelegate().getBytes());
      }
    } else {
      result.put(FrameworkConstants.RoutingContext.Json.BODY, new JsonObject());
    }

    JsonObject headers = new JsonObject(convertMultiMapIntoMap(pContext.request().headers()));
    result.put(FrameworkConstants.RoutingContext.Json.HEADERS, headers);

    JsonObject query = new JsonObject(convertMultiMapIntoMap(pContext.queryParams()));
    result.put(FrameworkConstants.RoutingContext.Json.QUERY_STRING_PARAM, query);

    JsonObject params = new JsonObject(convertMultiMapIntoMap(pContext.request().params()));
    result.put(FrameworkConstants.RoutingContext.Json.PATH_PARAM, params);

    getLog().debug(() -> "Context to JSON:" + result.toString());

    return result;
  }

  public HttpServerResponse buildResponseFromReply(JsonObject pReplyResponse, RoutingContext pContext) {

    JsonObject headers = pReplyResponse.getJsonObject(FrameworkConstants.RoutingContext.Json.HEADERS, new JsonObject());
    String statusCode = pReplyResponse.getString(FrameworkConstants.RoutingContext.Json.STATUS_CODE, HttpResponseStatus.OK.codeAsText().toString());

    if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
      headers.put(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
    }

    headers
            .fieldNames()
            .stream()
            .forEach((field) -> {
              pContext.response().putHeader(field, headers.getString(field, ""));
            });

    pContext.response().setStatusCode(HttpResponseStatus.parseLine(statusCode).code());

    Object body = pReplyResponse.getMap().get(FrameworkConstants.RoutingContext.Json.BODY);

    if (body != null) {
      String bodyStr = body.toString();
      pContext.response().putHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), Integer.toString(bodyStr.length()));
      pContext.response().write(bodyStr);
    }

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

    getServices()
            .getServices()
            .forEach((api) -> {
              RESTService service = (RESTService) api;

              apiFactory.addHandlerByOperationId(service.getOperationId(), (routingContext) -> {

                routingContext.put(FrameworkConstants.RoutingContext.Attribute.BODY_AS_JSON, service.isBodyAsJson());

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

  public HashMap<String, Timer> getMetricTimers() {
    return mMetricTimers;
  }

  public void setMetricTimers(HashMap<String, Timer> pMetricTimers) {
    this.mMetricTimers = pMetricTimers;
  }

  public MetricRegistry getMetricRegistry() {
    return mMetricRegistry;
  }

  public void setMetricRegistry(MetricRegistry pMetricRegistry) {
    this.mMetricRegistry = pMetricRegistry;
  }

}
