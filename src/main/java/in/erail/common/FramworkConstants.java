package in.erail.common;

/**
 *
 * @author vinay
 */
public class FramworkConstants {

  public static class Session {

    public static final String PRINCIPAL = "user";
  }

  public static class RoutingContext {

    public static class Json {

      public static final String HEADER = "header";
      public static final String PARAM = "param";
      public static final String QUERY = "query";
      public static final String BODY = "body";
    }

  }
  
  public static class SockJS {
    public static final String BRIDGE_EVENT_RAW_MESSAGE_ADDRESS = "address";
  }

}
