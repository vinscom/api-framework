package javax.persistence;

import io.reactivex.Single;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class EntityManagerHelper {

  private EntityManagerFactory mEntityManagerFactory;
  private Logger mLog;

  public Single<EntityManager> getEM() {
    return Single.using(() -> getEntityManagerFactory().createEntityManager(), em -> Single.just(em), em -> em.close(), false);
  }

  public Single<EntityManager> getEMTx() {
    return Single.using(() -> getEntityManagerFactory().createEntityManager(), em -> {
      em.getTransaction().begin();
      return Single.just(em);
    }, em -> {
      EntityTransaction tx = em.getTransaction();
      if (tx.isActive()) {
        getLog().error("Resource leakage: Entity Manager tx not finished. Rolling back tx");
        tx.rollback();
      }
      em.close();
    }, false);
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public EntityManagerFactory getEntityManagerFactory() {
    return mEntityManagerFactory;
  }

  public void setEntityManagerFactory(EntityManagerFactory pEntityManagerFactory) {
    this.mEntityManagerFactory = pEntityManagerFactory;
  }

}
