package in.erail.service;


import in.erail.model.RequestEvent;
import in.erail.model.ResponseEvent;

/**
 *
 * @author vinay
 */
public interface RESTService {
  String getOperationId();
  String getServiceUniqueId();
  ResponseEvent process(RequestEvent pRequest);
}
