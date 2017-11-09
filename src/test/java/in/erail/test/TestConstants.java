package in.erail.test;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author vinay
 */
public class TestConstants {

  public static final String ACCESS_TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3UU0zSVNoWkstZWtwaXlfODRXR2ZweThRZFhHa3NfVDNBUjNwVDBfOHBrIn0.eyJqdGkiOiJlNTI2YTNiMC04MDIwLTRiYTktOGFmZi02YzBmYTZkYjhhNGUiLCJleHAiOjE1MTAwNzc2MjIsIm5iZiI6MCwiaWF0IjoxNTEwMDc3MzIyLCJpc3MiOiJodHRwczovL29wZW5pZC5lcmFpbC5pbi9hdXRoL3JlYWxtcy9BUElHYXRld2F5IiwiYXVkIjoidGVzdF9lcmFpbF9pbiIsInN1YiI6ImNhMjUwN2Y3LTc0N2QtNGY5Ny04YTFkLTI4NGZmODQ1YWVhNCIsInR5cCI6IkJlYXJlciIsImF6cCI6InRlc3RfZXJhaWxfaW4iLCJub25jZSI6Im56QTJNamRmZmYiLCJhdXRoX3RpbWUiOjE1MTAwNzcyNTIsInNlc3Npb25fc3RhdGUiOiIyYjExNjEzOC1hMDdiLTQ3MjQtYTkzZi02NzM0YzRjMWM0ODIiLCJhY3IiOiIwIiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIkFQSV9WMV9UQVNLX1NFUlZJQ0UiLCJBUElfVjFfQlJPQURDQVNUX1NFUlZJQ0UiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInByZWZlcnJlZF91c2VybmFtZSI6ImFnZW50MSJ9.XrdyqCOaSNq2egPRXr8FdKHm3EqTnXTna37AK7JPeT_FDZSWDTsHvtW20hsW1lmq_7WoGFyt5SLFG_RTSA0GvfelArMDDwLhGIAM6UYEX3sNGvgHR0g3PQuiK-ACfYXdvrzWmJUANe0xxJBRa54Xpe7CkJg98qcDDP9-qvtmRgku6SGnFb9HBmkCiYs-D2G8Ems7TwDgxsFf4NVm2q5zPXmduJW4GFHCmBZK-8HwetmC4o8VORf0_lKhn-xF1Cw97qYkD4vzajQE9uW6hOiJYXZgWtp7D1lx79ts4_yMdppCdzMylv7lZppU6gezACdJwTyYQ1xBl9IZcEnv4qBEvg";

  public static class Service {

    public static class Broadcast {

      public static class APIMessage {

        public static final String PARAM_TOPIC_NAME = "topicName";
      }
    }

    public static class Message {

      public static class Json {

        public static final String STATUS = "status";
        public static final String DATA = "data";
      }

      public static JsonObject successMessage() {
        return new JsonObject().put(Json.STATUS, "success");
      }
    }

  }
}
