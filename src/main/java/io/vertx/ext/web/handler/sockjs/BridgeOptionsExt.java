package io.vertx.ext.web.handler.sockjs;

import in.erail.glue.annotation.StartService;
import java.util.List;

/**
 *
 * @author vinay
 */
public class BridgeOptionsExt extends BridgeOptions {

  private List<String> mInboundAddress;
  private List<String> mOutboundAddress;

  private List<String> mInboundAddressRegex;
  private List<String> mOutboundAddressRegex;

  private List<String> mInboundRequiredAuthority;
  private List<String> mOutboundRequiredAuthority;

  @StartService
  void start() {
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

}
