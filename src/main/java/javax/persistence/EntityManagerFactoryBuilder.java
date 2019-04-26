package javax.persistence;

import com.google.common.base.Strings;
import in.erail.glue.common.Util;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vinay
 */
public class EntityManagerFactoryBuilder {

  private static EntityManagerFactory mEntityManagerFactory;

  public static EntityManagerFactory create(String pPersistenceUnitName) {

    if (mEntityManagerFactory != null) {
      return mEntityManagerFactory;
    }

    String pun = pPersistenceUnitName.toLowerCase().replace("_", ".");

    Map<String,String> override = new HashMap<String, String>();
    String url = Util.getEnvironmentValue(pun + ".url");
    if (!Strings.isNullOrEmpty(url)) {
      override.put("javax.persistence.jdbc.url", url);
    }
    String user = Util.getEnvironmentValue(pun + ".user");
    if (!Strings.isNullOrEmpty(user)) {
      override.put("javax.persistence.jdbc.user", user);
    }
    String password = Util.getEnvironmentValue(pun + ".password");
    if (!Strings.isNullOrEmpty(password)) {
      override.put("javax.persistence.jdbc.password", password);
    }

    mEntityManagerFactory = Persistence.createEntityManagerFactory(pPersistenceUnitName, override);

    return mEntityManagerFactory;
  }

}
