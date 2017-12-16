package io.vertx.reactivex.ext.web.handler.sockjs;

/**
 *
 * @author vinay
 */
public class BridgeEventContext {

  private BridgeEvent mBridgeEvent;
  private String mAddress;
  private String mAddressKey;
  private String mId;

  public BridgeEvent getBridgeEvent() {
    return mBridgeEvent;
  }

  public void setBridgeEvent(BridgeEvent pBridgeEvent) {
    this.mBridgeEvent = pBridgeEvent;
  }

  public String getAddress() {
    return mAddress;
  }

  public void setAddress(String pAddress) {
    this.mAddress = pAddress;
  }

  /**
   * Unique event id set only during debug. And can be used for tracking
   * event
   * @return 
   */
  public String getId() {
    return mId;
  }

  public void setId(String pId) {
    this.mId = pId;
  }

  /**
   * Unique Key can be used for referring this event in external system(Like Redis).
   * Key changes whenever whole cluster is restarted.
   * @return 
   */
  public String getAddressKey() {
    return mAddressKey;
  }

  public void setAddressKey(String pAddressKey) {
    this.mAddressKey = pAddressKey;
  }
  
}
