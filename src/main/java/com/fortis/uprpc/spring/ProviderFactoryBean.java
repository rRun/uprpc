package com.fortis.uprpc.spring;

import com.fortis.uprpc.model.ProviderService;
import com.fortis.uprpc.netty.NettyServer;
import com.fortis.uprpc.util.IPHelper;
import com.fortis.uprpc.zookeeper.IRegisterCenter4Invoker;
import com.fortis.uprpc.zookeeper.IRegisterCenter4Provider;
import com.fortis.uprpc.zookeeper.impl.RegisterCenter;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Method;
import java.util.List;

public class ProviderFactoryBean implements FactoryBean,InitializingBean{

  //服务接口
  private Class<?> serviceItf;
  //服务实现
  private Object serviceObject;
  //服务端口
  private String serverPort;
  //服务超时时间
  private long timeout;
  //服务代理对象
  private Object serviceProxyObject;
  //服务提供者唯一的标示
  private String appKey;
  //服务分组组名
  private String groupName = "default";
  //服务提供者权重【1-100】
  private int weight = 1;
  //服务端线程数，默认提供10个
  private int workThreads = 10;


  @Override
  public Object getObject() throws Exception {
    return serviceProxyObject;
  }

  @Override
  public Class<?> getObjectType() {
    return serviceItf;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    //启动netty服务器
    NettyServer.singleton().start(Integer.parseInt(serverPort));
    //注册到zookeeper,元数据注册中心
    List<ProviderService> providerServiceList = buildProviderServiceInfos();
    IRegisterCenter4Provider registerCenter4Provider = RegisterCenter.singleton();
    registerCenter4Provider.registerProvider(providerServiceList);
  }

  private List<ProviderService> buildProviderServiceInfos(){
    List<ProviderService> providerList = Lists.newArrayList();
    Method[] methods = serviceObject.getClass().getDeclaredMethods();
    for (Method method : methods){
      ProviderService providerService = new ProviderService();
      providerService.setServiceItf(serviceItf);
      providerService.setServiceObject(serviceObject);
      providerService.setServerIp(IPHelper.localIp());
      providerService.setServerPort(Integer.parseInt(serverPort));
      providerService.setAppKey(appKey);
      providerService.setServiceMethod(method);
      providerService.setGroupName(groupName);
      providerService.setWeight(weight);
      providerService.setWorkThreads(workThreads);
      providerList.add(providerService);
    }
    return providerList;
  }


  //getter and setter
  public Class<?> getServiceItf() {
    return serviceItf;
  }

  public void setServiceItf(Class<?> serviceItf) {
    this.serviceItf = serviceItf;
  }

  public Object getServiceObject() {
    return serviceObject;
  }

  public void setServiceObject(Object serviceObject) {
    this.serviceObject = serviceObject;
  }

  public String getServerPort() {
    return serverPort;
  }

  public void setServerPort(String servicePort) {
    this.serverPort = servicePort;
  }

  public long getTimeout() {
    return timeout;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  public Object getServiceProxyObject() {
    return serviceProxyObject;
  }

  public void setServiceProxyObject(Object serviceProxyObject) {
    this.serviceProxyObject = serviceProxyObject;
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

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public int getWorkThreads() {
    return workThreads;
  }

  public void setWorkThreads(int workThreads) {
    this.workThreads = workThreads;
  }
}
