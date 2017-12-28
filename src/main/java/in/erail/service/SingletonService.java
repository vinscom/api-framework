package in.erail.service;

import io.reactivex.Completable;

/**
 *
 * @author vinay
 */
public interface SingletonService {

  Completable startService();
  
}
