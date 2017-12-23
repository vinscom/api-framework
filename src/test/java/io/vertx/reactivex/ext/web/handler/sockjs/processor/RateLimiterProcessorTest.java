package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Rule;
import in.erail.glue.Glue;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSSocket;
import static org.mockito.Mockito.*;

/**
 *
 * @author vinay
 */
@RunWith(VertxUnitRunner.class)
public class RateLimiterProcessorTest {

  @Rule
  public Timeout rule = Timeout.seconds(2000);

  @Test
  public void testSendLimit(TestContext context) {

    Async async = context.async();

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
            .doOnError((err) -> {
              context.fail("Limit rate should fail 10 time");
            })
            .doFinally(() -> async.complete())
            .subscribe();
  }

  @Test
  public void testPublishLimit(TestContext context) {

    Async async = context.async();

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
            .doOnError((err) -> {
              context.fail("Limit rate should fail 70 time");
            })
            .doFinally(() -> async.complete())
            .subscribe();
  }
}
