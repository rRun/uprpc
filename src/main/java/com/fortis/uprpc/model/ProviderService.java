package com.fortis.uprpc.model;

import java.io.Serializable;
import java.lang.reflect.Method;

public class ProviderService implements Serializable{

  //IP和端口
  private int serverPort;
  private String serverIp;

  //服务实现
  private Object serviceObject;
  //服务实现接口
  private Class<?> serviceItf;
  //服务实现方法
  private Method serviceMethod;

  //服务提供者唯一的标示
  private String appKey;
  //服务分组组名
  private String groupName = "default";
  //服务端线程数，默认提供10个
  private int workThreads = 10;
  //超时
  private int timeout;
  //服务提供者权重【1-100】
  private int weight = 1;

  public Class<?> getServiceItf() {
    return serviceItf;
  }

  public void setServiceItf(Class<?> serviceItf) {
    this.serviceItf = serviceItf;
  }

  public int getServerPort() {
    return serverPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  public String getServerIp() {
    return serverIp;
  }

  public void setServerIp(String serverIp) {
    this.serverIp = serverIp;
  }

  public Object getServiceObject() {
    return serviceObject;
  }

  public void setServiceObject(Object serviceObject) {
    this.serviceObject = serviceObject;
  }

  public Method getServiceMethod() {
    return serviceMethod;
  }

  public void setServiceMethod(Method serviceMethod) {
    this.serviceMethod = serviceMethod;
  }

  public String getAppKey() {
    return appKey;
  }

  public void setAppKey(String appKey) {
    this.appKey = appKey;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public int getWorkThreads() {
    return workThreads;
  }

  public void setWorkThreads(int workThreads) {
    this.workThreads = workThreads;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public ProviderService copy() {
    ProviderService providerService = new ProviderService();
    providerService.setServiceItf(this.getServiceItf());
    providerService.setServiceObject(this.getServiceObject());
    providerService.setServiceMethod(this.getServiceMethod());
    providerService.setServerIp(this.getServerIp());
    providerService.setServerPort(this.getServerPort());
    providerService.setTimeout(this.getTimeout());
    providerService.setWeight(this.getWeight());
    providerService.setWorkThreads(this.getWorkThreads());
    providerService.setAppKey(this.getAppKey());
    providerService.setGroupName(this.getGroupName());
    return providerService;
  }
}
