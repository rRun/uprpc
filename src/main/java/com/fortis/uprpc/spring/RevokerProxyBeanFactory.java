package com.fortis.uprpc.spring;

import com.fortis.uprpc.cluster.ClusterEngine;
import com.fortis.uprpc.cluster.ClusterStrategy;
import com.fortis.uprpc.model.ProviderService;
import com.fortis.uprpc.model.UpRequest;
import com.fortis.uprpc.model.UpResponse;
import com.fortis.uprpc.zookeeper.IRegisterCenter4Invoker;
import com.fortis.uprpc.zookeeper.impl.RegisterCenter;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    newProvider.setServiceMethod(method);
    newProvider.setServiceItf(targetInterface);

    //构建请求
    final UpRequest request = new UpRequest();
    request.setUniqueKey(UUID.randomUUID().toString()+"_"+Thread.currentThread().getId());
    request.setProviderService(newProvider);
    request.setInvokeTimeout(consumeTimeout);
    request.setInvokedMethodName(method.getName());
    request.setArgs(args);

    try {
      if (fixedThreadPool == null){
        synchronized (RevokerProxyBeanFactory.class){
          if (null == fixedThreadPool){
            fixedThreadPool = Executors.newFixedThreadPool(threadWorkerNumber);
          }
        }
      }
      String serverIp = request.getProviderService().getServerIp();
      int port = request.getProviderService().getServerPort();
      InetSocketAddress inetSocketAddress = new InetSocketAddress(serverIp,port);
      Future<UpResponse> responseFuture = fixedThreadPool.submit(new RevokerServiceCallable(inetSocketAddress,request));
      UpResponse response = responseFuture.get(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);
      if (response != null)
        return response.getResult();
    }catch (Exception e){
      throw new RuntimeException(e);
    }
    return null;
  }
}
