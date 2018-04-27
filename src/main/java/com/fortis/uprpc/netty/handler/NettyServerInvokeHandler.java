package com.fortis.uprpc.netty.handler;

import com.fortis.uprpc.model.ProviderService;
import com.fortis.uprpc.model.UpRequest;
import com.fortis.uprpc.model.UpResponse;
import com.fortis.uprpc.zookeeper.IRegisterCenter4Provider;
import com.fortis.uprpc.zookeeper.impl.RegisterCenter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class NettyServerInvokeHandler extends SimpleChannelInboundHandler{

  //服务端限流
  private static final Map<String,Semaphore> serviceKeySemaphoreMap = Maps.newConcurrentMap();
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    UpRequest request = (UpRequest)msg;
    if (ctx.channel().isWritable()){
      ProviderService metaDataModel = request.getProviderService();
      long consumeTimeOut = request.getInvokeTimeout();
      final String methodName = request.getInvokedMethodName();

      //获取服务提供者类名
      String seviceKey = metaDataModel.getServiceItf().getName();

      //获取限流工具类
      int workerThread = metaDataModel.getWorkThreads();
      Semaphore semaphore = serviceKeySemaphoreMap.get(seviceKey);
      if (semaphore == null){
        synchronized (serviceKeySemaphoreMap){
          semaphore = serviceKeySemaphoreMap.get(seviceKey);
          if (semaphore == null){
            semaphore = new Semaphore(workerThread);
            serviceKeySemaphoreMap.put(seviceKey,semaphore);
          }
        }
      }

      //获取注册中心服务
      IRegisterCenter4Provider registerCenter4Provider = RegisterCenter.singleton();
      List<ProviderService> localServiceCaches = registerCenter4Provider.getProviderServiceMap().get(seviceKey) ;

      Object result = null;
      boolean acqurie = false;

      try{
        ProviderService localProviderService = Collections2.filter(localServiceCaches, new com.google.common.base.Predicate<ProviderService>() {
          @Override
          public boolean apply(ProviderService input) {
           return StringUtils.equals(methodName,input.getServiceItf().getName());
          }
        }).iterator().next();
        Object serviceObject = localProviderService.getServiceObject();
        Method method = localProviderService.getServiceMethod();
        acqurie = semaphore.tryAcquire(consumeTimeOut, TimeUnit.MILLISECONDS);
        if (acqurie){
          result = method.invoke(serviceObject,request.getArgs());
        }
      }catch (Exception e){
        System.out.println("e"+e.getLocalizedMessage());
        result = e;
      }finally {
        if (acqurie)
          semaphore.release();
      }

      UpResponse response = new UpResponse();
      response.setInvokeTimeout(consumeTimeOut);
      response.setUniqueKey(request.getUniqueKey());
      response.setResult(result);

      ctx.writeAndFlush(response);
    }else {
      System.out.println("other ingnore. ");
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }

}
