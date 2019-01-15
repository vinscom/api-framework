package in.erail.model;

/**
 *
 * @author vinay
 */
public class Event {

  private RequestEvent request;
  private ResponseEvent response;

  public Event() {
    this(new RequestEvent(), new ResponseEvent());
  }

  public Event(RequestEvent pRequest, ResponseEvent pResponse) {
    this.request = pRequest;
    this.response = pResponse;
  }
  
  public RequestEvent getRequest() {
    return request;
  }

  public Event setRequest(RequestEvent pRequest) {
    this.request = pRequest;
    return this;
  }

  public ResponseEvent getResponse() {
    return response;
  }

  public Event setResponse(ResponseEvent pResponse) {
    this.response = pResponse;
    return this;
  }

}
