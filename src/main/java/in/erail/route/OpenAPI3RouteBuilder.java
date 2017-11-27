package in.erail.route;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import in.erail.common.FramworkConstants;
import in.erail.glue.component.ServiceArray;
import in.erail.service.Service;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;

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

  public void process(RoutingContext pRequestContext, String pServiceUniqueId) {

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
                    });

  }

  public JsonObject serialiseRoutingContext(RoutingContext pContext) {

    JsonObject result = new JsonObject();
    result.put(FramworkConstants.RoutingContext.Json.BODY, pContext.getBodyAsJson());

    JsonObject headers = new JsonObject(convertMultiMapIntoMap(pContext.request().headers()));
    result.put(FramworkConstants.RoutingContext.Json.HEADER, headers);

    JsonObject query = new JsonObject(convertMultiMapIntoMap(pContext.queryParams()));
    result.put(FramworkConstants.RoutingContext.Json.QUERY, query);

    JsonObject params = new JsonObject(convertMultiMapIntoMap(pContext.request().params()));
    result.put(FramworkConstants.RoutingContext.Json.PARAM, params);

    getLog().debug(() -> "Context to JSON:" + result.toString());

    return result;
  }

  public HttpServerResponse buildResponseFromReply(JsonObject pReplyResponse, RoutingContext pContext) {

    JsonObject body = pReplyResponse.getJsonObject(FramworkConstants.RoutingContext.Json.BODY, new JsonObject());
    JsonObject headers = pReplyResponse.getJsonObject(FramworkConstants.RoutingContext.Json.HEADER, new JsonObject());
    String statusCode = pReplyResponse.getString(FramworkConstants.RoutingContext.Json.STATUS_CODE, HttpResponseStatus.OK.codeAsText().toString());

    headers
            .fieldNames()
            .stream()
            .forEach((field) -> {
              pContext.response().putHeader(field, headers.getString(field, ""));
            });
    
    pContext.response().setStatusCode(HttpResponseStatus.parseLine(statusCode).code());

    String bodyStr = body.toString();
    pContext.response().putHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), Integer.toString(bodyStr.length()));
    pContext.response().write(body.toString());

    return pContext.response();
  }

  public Map<String, Object> convertMultiMapIntoMap(MultiMap pMultiMap) {
    return pMultiMap
            .getDelegate()
            .entries()
            .stream()
            .collect(Collectors.toMap((t) -> t.getKey(), (t) -> t.getValue()));
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
              Service service = (Service) api;
              apiFactory.addHandlerByOperationId(service.getOperationId(), (routingContext) -> {
                if (isSecurityEnable()) {
                  routingContext.user().isAuthorized(AUTHORIZATION_PREFIX + ":" + service.getServiceUniqueId(), (event) -> {
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
  
}
