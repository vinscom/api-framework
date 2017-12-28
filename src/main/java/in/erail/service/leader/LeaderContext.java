/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.erail.service.leader;

/**
 *
 * @author vinay
 */

public class LeaderContext {

  private String mAddress;  //Topic for which leader is getting selected
  private String mConfirmationAddress;  //Address on which client needs to send confirmation to become leader
  private String mLeaderSessionId;  //Session becoming leader
  private String mReplayAddress;  //Reply address of message recieved on confirmation address

  public String getAddress() {
    return mAddress;
  }

  public LeaderContext setAddress(String pAddress) {
    this.mAddress = pAddress;
    return this;
  }

  public String getConfirmationAddress() {
    return mConfirmationAddress;
  }

  public LeaderContext setConfirmationAddress(String pConfirmationAddress) {
    this.mConfirmationAddress = pConfirmationAddress;
    return this;
  }

  public String getLeaderSessionId() {
    return mLeaderSessionId;
  }

  public LeaderContext setLeaderSessionId(String pLeaderSessionId) {
    this.mLeaderSessionId = pLeaderSessionId;
    return this;
  }

  public String getReplayAddress() {
    return mReplayAddress;
  }

  public LeaderContext setReplayAddress(String pReplayAddress) {
    this.mReplayAddress = pReplayAddress;
    return this;
  }

}
