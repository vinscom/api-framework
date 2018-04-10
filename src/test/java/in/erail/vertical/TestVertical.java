package in.erail.vertical;


import io.vertx.reactivex.core.AbstractVerticle;

/**
 *
 * @author vinay
 */
public class TestVertical extends AbstractVerticle {

  @Override
  public void start() {
    vertx.eventBus().send("vertical.test.java", "success");
  }
  
}
