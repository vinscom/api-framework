package in.erail.route;

import in.erail.service.Service;
import in.erail.common.FramworkConstants;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import in.erail.glue.component.ServiceArray;

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
                    convertContextIntoJson(pRequestContext),
                    getDeliveryOptions(),
                    (reply) -> {
                      if (reply.succeeded()) {
                        pRequestContext
                                .response()
                                .setStatusCode(200)
                                .end(reply.result().body().toString());
                      } else {
                        getLog().error(() -> "Error in reply:" + reply.cause().toString());
                        pRequestContext
                                .response()
                                .setStatusCode(400)
                                .end(reply.cause().toString());
                      }
                    });

  }

  public JsonObject convertContextIntoJson(RoutingContext pContext) {

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
