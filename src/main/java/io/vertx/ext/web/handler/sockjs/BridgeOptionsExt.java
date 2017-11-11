package io.vertx.ext.web.handler.sockjs;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import in.erail.glue.annotation.StartService;
import java.util.List;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vinay
 */
public class BridgeOptionsExt extends BridgeOptions {

  private Logger mLog;
  private List<String> mInboundAddress;
  private List<String> mOutboundAddress;

  private List<String> mInboundAddressRegex;
  private List<String> mOutboundAddressRegex;

  private List<String> mInboundRequiredAuthority;
  private List<String> mOutboundRequiredAuthority;

  @StartService
  public void start() {

    getLog().debug(() -> {
      return MoreObjects
              .toStringHelper(BridgeOptionsExt.class.getCanonicalName())
              .add("InboundAddress", Joiner.on(",").join(mInboundAddress))
              .add("OutboundAddress", Joiner.on(",").join(mOutboundAddress))
              .add("InboundAddressRegex", Joiner.on(",").join(mInboundAddressRegex))
              .add("OutboundAddressRegex", Joiner.on(",").join(mOutboundAddressRegex))
              .add("InboundRequiredAuthority", Joiner.on(",").join(mInboundRequiredAuthority))
              .add("OutboundRequiredAuthority", Joiner.on(",").join(mOutboundRequiredAuthority))
              .toString();
    });

    getInboundAddress()
            .stream()
            .forEachOrdered((rule) -> {
              io.vertx.ext.bridge.PermittedOptions option = new io.vertx.ext.bridge.PermittedOptions();
              option.setAddress(rule);
              addInboundPermitted(option);
            });

    getOutboundAddress()
            .stream()
            .forEachOrdered((rule) -> {
              io.vertx.ext.bridge.PermittedOptions option = new io.vertx.ext.bridge.PermittedOptions();
              option.setAddress(rule);
              addOutboundPermitted(option);
            });

    getInboundAddressRegex()
            .stream()
            .forEachOrdered((rule) -> {
              io.vertx.ext.bridge.PermittedOptions option = new io.vertx.ext.bridge.PermittedOptions();
              option.setAddressRegex(rule);
              addInboundPermitted(option);
            });

    getOutboundAddressRegex()
            .stream()
            .forEachOrdered((rule) -> {
              io.vertx.ext.bridge.PermittedOptions option = new io.vertx.ext.bridge.PermittedOptions();
              option.setAddressRegex(rule);
              addOutboundPermitted(option);
            });

    getInboundRequiredAuthority()
            .stream()
            .forEachOrdered((rule) -> {
              io.vertx.ext.bridge.PermittedOptions option = new io.vertx.ext.bridge.PermittedOptions();
              option.setRequiredAuthority(rule);
              addInboundPermitted(option);
            });

    getOutboundRequiredAuthority()
            .stream()
            .forEachOrdered((rule) -> {
              io.vertx.ext.bridge.PermittedOptions option = new io.vertx.ext.bridge.PermittedOptions();
              option.setRequiredAuthority(rule);
              addOutboundPermitted(option);
            });
  }

  public List<String> getInboundAddress() {
    return mInboundAddress;
  }

  public void setInboundAddress(List<String> pInboundAddress) {
    this.mInboundAddress = pInboundAddress;
  }

  public List<String> getOutboundAddress() {
    return mOutboundAddress;
  }

  public void setOutboundAddress(List<String> pOutboundAddress) {
    this.mOutboundAddress = pOutboundAddress;
  }

  public List<String> getInboundAddressRegex() {
    return mInboundAddressRegex;
  }

  public void setInboundAddressRegex(List<String> pInboundAddressRegex) {
    this.mInboundAddressRegex = pInboundAddressRegex;
  }

  public List<String> getOutboundAddressRegex() {
    return mOutboundAddressRegex;
  }

  public void setOutboundAddressRegex(List<String> pOutboundAddressRegex) {
    this.mOutboundAddressRegex = pOutboundAddressRegex;
  }

  public List<String> getInboundRequiredAuthority() {
    return mInboundRequiredAuthority;
  }

  public void setInboundRequiredAuthority(List<String> pInboundRequiredAuthority) {
    this.mInboundRequiredAuthority = pInboundRequiredAuthority;
  }

  public List<String> getOutboundRequiredAuthority() {
    return mOutboundRequiredAuthority;
  }

  public void setOutboundRequiredAuthority(List<String> pOutboundRequiredAuthority) {
    this.mOutboundRequiredAuthority = pOutboundRequiredAuthority;
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

}
