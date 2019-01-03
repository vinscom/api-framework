package in.erail.test;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author vinay
 */
public class TestConstants {

  public static final String ACCESS_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJwZXJtaXNza"
          + "W9ucyI6W10sImlhdCI6MTU0NjIxNzI2Mywic3ViIjoidGVzdHVzZXIifQ.LKd5wNfsmNWIGh6j5rl0xvr"
          + "MdII7_doX6pF-qs0qvf7aIffbUpKoGAA36EQHU04D5WyikbciI7PseIw9YakV2yiIK798dD8Av4u4D5P2"
          + "-1UGMa9dmD0jR_G15C2LsiSRLx-njJI_qmq5Iuu1ud4QKL67-tah40I_HcZWrAgywuZ143Fw0f5rf4wHs"
          + "unx7Cm_c5UykvNjMZbxo_Ati6py3hsvydZq6f1CfMU0mPST9Lq4FwzdPe_sglt9F5rTB95oIYxVkPakzz"
          + "nVcgDojkuGbFXcPTFLtwMxDXOD0tm8dwWYxKqokFRpQcl7ni0AiJfn1VeuCxG8QjCmtK_ti818pg";
  
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
