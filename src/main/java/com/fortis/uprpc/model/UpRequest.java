package com.fortis.uprpc.model;

import java.io.Serializable;

public class UpRequest implements Serializable {
  //唯一一次标示，UUID
  private String uniqueKey;
  //服务提供者信息
  private ProviderService providerService;
  //调用方法
  private String invokedMethodName;
  //方法参数
  private Object[] args;
  //应用名
  private String appName;
  //请求超时时间
  private long invokeTimeout;

  public String getUniqueKey() {
    return uniqueKey;
  }

  public void setUniqueKey(String uniqueKey) {
    this.uniqueKey = uniqueKey;
  }

  public ProviderService getProviderService() {
    return providerService;
  }

  public void setProviderService(ProviderService providerService) {
    this.providerService = providerService;
  }

  public String getInvokedMethodName() {
    return invokedMethodName;
  }

  public void setInvokedMethodName(String invokedMethodName) {
    this.invokedMethodName = invokedMethodName;
  }

  public Object[] getArgs() {
    return args;
  }

  public void setArgs(Object[] args) {
    this.args = args;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public long getInvokeTimeout() {
    return invokeTimeout;
  }

  public void setInvokeTimeout(long invokeTimeout) {
    this.invokeTimeout = invokeTimeout;
  }
}
