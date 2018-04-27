package com.fortis.uprpc.spring;

import com.fortis.uprpc.cluster.ClusterEngine;
import com.fortis.uprpc.cluster.ClusterStrategy;
import com.fortis.uprpc.model.ProviderService;
import com.fortis.uprpc.zookeeper.IRegisterCenter4Invoker;
import com.fortis.uprpc.zookeeper.impl.RegisterCenter;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 消费端bean代理工厂
 */
public class RevokerProxyBeanFactory implements InvocationHandler{
  private ExecutorService fixedThreadPool = null;

  //服务接口
  private Class<?> targetInterface;
  //超时时间
  private int consumeTimeout;
  //调用者线程
  private static int threadWorkerNumber = 10;
  //负载均衡策略
  private String clusterStrategy;

  public RevokerProxyBeanFactory(Class<?> targetInterface,int consumeTimeout,String clusterStrategy){
    this.targetInterface = targetInterface;
    this.clusterStrategy = clusterStrategy;
    this.consumeTimeout = consumeTimeout;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    String serviceKey = targetInterface.getName();
    IRegisterCenter4Invoker registerCenter4Invoker = RegisterCenter.singleton();
    List<ProviderService> providerServices = registerCenter4Invoker.getServiceMetaDataMap4Consume().get(serviceKey);
    ClusterStrategy clusterStrategyService = ClusterEngine.queryClusterStrategy(clusterStrategy);
    ProviderService providerService = clusterStrategyService.select(providerServices);

    ProviderService newProvider = providerService.copy();
    return null;
  }
}
