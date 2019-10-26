package in.erail.server;

import in.erail.route.RouterBuilder;
import org.apache.logging.log4j.Logger;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.LoggerHandler;
import in.erail.glue.Glue;
import in.erail.glue.annotation.StartService;

/**
 *
 * @author vinay
 */
public class Server {

  private Vertx mVertx;
  private String[] mRouterBuilder;
  private String[] mMountPath;
  private Router[] mRouter;
  private Logger mLog;
  private HttpServerOptions mHttpServerOptions;
  private HttpServer mHttpServer;

  @StartService
  public void start() {

    HttpServer server = getVertx().createHttpServer(getHttpServerOptions());

    Router router = Router.router(getVertx());

    // Logging
    if (getLog().isDebugEnabled()) {
      router.route("/*").handler(LoggerHandler.create());
    }

    for (int i = 0; i < mMountPath.length; i++) {
      router.mountSubRouter(mMountPath[i], mRouter[i]);
    }

    mHttpServer = server
            .requestHandler(router)
            .rxListen()
            .blockingGet();

    getLog().info(() -> String.format("---------------Server[%s:%s] is ready-----------------", getHttpServerOptions().getHost(), getHttpServerOptions().getPort()));
  }

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  public String[] getRouterBuilder() {
    return mRouterBuilder;
  }

  public void setRouterBuilder(String[] pRouterBuilder) {

    this.mRouterBuilder = pRouterBuilder;

    mMountPath = new String[pRouterBuilder.length];
    mRouter = new Router[pRouterBuilder.length];

    for (int i = 0; i < pRouterBuilder.length; i++) {
      String[] kv = pRouterBuilder[i].split("=");
      Object component = Glue.instance().resolve(kv[1]);
      String route = kv[0];
      mMountPath[i] = route;
      mRouter[i] = ((RouterBuilder) component).getRouter();
    }
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public HttpServerOptions getHttpServerOptions() {
    return mHttpServerOptions;
  }

  public void setHttpServerOptions(HttpServerOptions pHttpServerOptions) {
    this.mHttpServerOptions = pHttpServerOptions;
  }

  public HttpServer getHttpServer() {
    return mHttpServer;
  }

  public void setHttpServer(HttpServer pHttpServer) {
    this.mHttpServer = pHttpServer;
  }

}
