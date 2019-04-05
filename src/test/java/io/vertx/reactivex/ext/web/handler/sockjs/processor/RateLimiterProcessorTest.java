package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import in.erail.glue.Glue;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSSocket;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
public class RateLimiterProcessorTest {

  @Test
  public void testSendLimit(VertxTestContext testContext) {

    RateLimiterProcessor service = Glue.instance().resolve("/io/vertx/ext/web/handler/sockjs/processor/RateLimiterProcessor");

    BridgeEventContext bec = new BridgeEventContext();

    SockJSSocket socket = mock(SockJSSocket.class);
    BridgeEvent be = mock(BridgeEvent.class);

    when(be.type()).thenReturn(BridgeEventType.SEND);
    when(be.socket()).thenReturn(socket);
    when(socket.writeHandlerID()).thenReturn("FAKE_HANDLE");

    bec.setBridgeEvent(be);

    Observable
            .range(0, 130)
            .flatMapSingle((i) -> service.process(Single.just(bec)))
            .doOnComplete(() -> {
              verify(be, times(10)).fail(anyString());
            })
            .ignoreElements()
            .subscribe(() -> testContext.completeNow(),err -> testContext.failNow(err));
  }

  @Test
  public void testPublishLimit(VertxTestContext testContext) {

    RateLimiterProcessor service = Glue.instance().resolve("/io/vertx/ext/web/handler/sockjs/processor/RateLimiterProcessor");

    BridgeEventContext bec = new BridgeEventContext();

    SockJSSocket socket = mock(SockJSSocket.class);
    BridgeEvent be = mock(BridgeEvent.class);

    when(be.type()).thenReturn(BridgeEventType.PUBLISH);
    when(be.socket()).thenReturn(socket);
    when(socket.writeHandlerID()).thenReturn("FAKE_HANDLE");

    bec.setBridgeEvent(be);

    Observable
            .range(0, 130)
            .flatMapSingle((i) -> service.process(Single.just(bec)))
            .doOnComplete(() -> {
              verify(be, times(70)).fail(anyString());
            })
            .ignoreElements()
            .subscribe(() -> testContext.completeNow(),err -> testContext.failNow(err));
  }
}
