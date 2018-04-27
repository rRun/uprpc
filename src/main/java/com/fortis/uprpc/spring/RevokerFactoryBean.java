package com.fortis.uprpc.spring;

import com.fortis.uprpc.model.ProviderService;
import com.fortis.uprpc.zookeeper.IRegisterCenter4Invoker;
import com.fortis.uprpc.zookeeper.impl.RegisterCenter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;

public class RevokerFactoryBean implements FactoryBean,InitializingBean{

  //服务接口
  private Class<?> targetInterface;
  //超时时间
  private int timeout;
  //服务bean
  private Object serviceObject;
  //负载均衡策略
  private String clusterStrategy;
  //服务提供者唯一标示
  private String remoteAppKey;
  private String groupName = "default";

  @Override
  public Object getObject() throws Exception {
    return serviceObject;
  }

  @Override
  public Class<?> getObjectType() {
    return targetInterface;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    //获取注册中心
    IRegisterCenter4Invoker registerCenter4Invoker = RegisterCenter.singleton();
    //初始化服务提供列表到本地缓存
    registerCenter4Invoker.initProviderMap(remoteAppKey,groupName);

    Map<String,List<ProviderService>> providerMap = registerCenter4Invoker.getServiceMetaDataMap4Consume();
    if (MapUtils.isEmpty(providerMap)){
      throw new RuntimeException("service provider list is empty .");
    }
    //TODO

  }

  //getter and setter
  public Class<?> getTargetInterface() {
    return targetInterface;
  }

  public void setTargetInterface(Class<?> targetInterface) {
    this.targetInterface = targetInterface;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public Object getServiceObject() {
    return serviceObject;
  }

  public void setServiceObject(Object serviceObject) {
    this.serviceObject = serviceObject;
  }

  public String getClusterStrategy() {
    return clusterStrategy;
  }

  public void setClusterStrategy(String clusterStrategy) {
    this.clusterStrategy = clusterStrategy;
  }

  public String getRemoteAppKey() {
    return remoteAppKey;
  }

  public void setRemoteAppKey(String remoteAppKey) {
    this.remoteAppKey = remoteAppKey;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
}
