package io.vertx.reactivex.ext.web.handler.sockjs;

import java.io.IOException;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.bridge.BridgeEventType;

/**
 *
 * @author vinay
 */
public class BridgeEventUpdate {

  private BridgeEventType mType;
  private String mAddress;
  private String mSession;

  public BridgeEventType getType() {
    return mType;
  }

  public void setType(BridgeEventType pType) {
    this.mType = pType;
  }

  public String getAddress() {
    return mAddress;
  }

  public void setAddress(String pAddress) {
    this.mAddress = pAddress;
  }

  public String getSession() {
    return mSession;
  }

  public void setSession(String pSession) {
    this.mSession = pSession;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("type", getType().toString());
    json.put("address", getAddress());
    json.put("session", getSession());
    return json;
  }

  public static BridgeEventUpdate parse(JsonObject pJson) throws IOException {
    return DatabindCodec.mapper().readValue(pJson.encode(), BridgeEventUpdate.class);
  }

  @Override
  public String toString() {
    return toJson().toString();
  }
}
