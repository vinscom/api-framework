package in.erail.security;

import in.erail.glue.Glue;
import static org.junit.jupiter.api.Assertions.*;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
public class SecurityToolsTest {

  @Test
  public void testEncryptDecrypt(VertxTestContext testContext) {
    SecurityTools securityTools = Glue.instance().resolve("/in/erail/security/SecurityTools");
    String text = "2b9b7d16-cfa3-40a5-b8e0-32955384df3b";

    String a = securityTools.encrypt(text);
    String b = securityTools.encrypt(text);

    assertNotEquals(a, b);

    String a1 = securityTools.decrypt(a);
    String b1 = securityTools.decrypt(b);

    assertEquals(text, a1);
    assertEquals(text, b1);
    testContext.completeNow();
  }

}
