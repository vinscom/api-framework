package in.erail.common;

/**
 *
 * @author vinay
 */
public class FrameworkConstants {

  public static class Session {

    public static final String PRINCIPAL = "user";
  }

  public static class RoutingContext {

    public static class Json {

      public static final String COOKIES = "cookies";

      public static class Cookie {

        public static final String NAME = "name";
        public static final String VALUE = "value";
        public static final String HTTP_ONLY = "httpOnly";
        public static final String SECURE = "secure";
        public static final String PATH = "path";
        public static final String MAX_AGE = "maxAge";
        public static final String DOMAIN = "domain";
      }

      public static final String HEADERS = "headers";
      public static final String PATH_PARAM = "pathParameters";
      public static final String QUERY_STRING_PARAM = "queryStringParameters";
      public static final String BODY = "body";
      public static final String STATUS_CODE = "statusCode";
      public static final String IS_BASE64_ENCODED = "isBase64Encoded";
    }
  }

  public static class SockJS {

    public static final String BRIDGE_EVENT_RAW_MESSAGE_ADDRESS = "address";
    public static final String BRIDGE_EVENT_RAW_MESSAGE_HEADERS = "headers";
  }

}
