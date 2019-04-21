package in.erail.scheduler;

import in.erail.glue.Glue;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
public class QuartzSchedulerTest {

  @Test
  public void testEncryptDecrypt(VertxTestContext testContext) {
    HelloScheduler sch = Glue.instance().resolve("/in/erail/scheduler/HelloScheduler");
    sch.setVertxTestContext(testContext);
  }

}
