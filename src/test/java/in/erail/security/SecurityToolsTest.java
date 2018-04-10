package in.erail.security;

import in.erail.glue.Glue;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 *
 * @author vinay
 */
@RunWith(VertxUnitRunner.class)
public class SecurityToolsTest {
  
  @Rule
  public Timeout rule = Timeout.seconds(2000);

  private SecurityTools securityTools;
  
  public SecurityToolsTest() {
  }
  
  @Before
  public void setUp() {
    securityTools = Glue.instance().resolve("/in/erail/security/SecurityTools");
  }


  @Test
  public void testEncryptDecrypt(TestContext context) {
    String text = "2b9b7d16-cfa3-40a5-b8e0-32955384df3b";
    
    String a = securityTools.encrypt(text);
    String b = securityTools.encrypt(text);

    context.assertNotEquals(a, b);
    
    String a1 = securityTools.decrypt(a);
    String b1 = securityTools.decrypt(b);
    
    context.assertEquals(text, a1);
    context.assertEquals(text, b1);
  }

  
}
