package com.fortis.uprpc.netty.handler;

import com.fortis.uprpc.model.UpResponse;
import com.fortis.uprpc.model.UpResponseWrapper;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RevokerResponseHolder {
  //服务返回结果map
  private static final Map<String,UpResponseWrapper> responseMap = Maps.newConcurrentMap();

  //清除过期的返回结果
  private static final ExecutorService remoteExpireKeyExecutor = Executors.newSingleThreadExecutor();

  static {
    //删除超时未获取到的结果key,防止内存泄露
    remoteExpireKeyExecutor.execute(new Runnable() {
      @Override
      public void run() {
        while (true){
          try{
            for (Map.Entry<String,UpResponseWrapper> entry : responseMap.entrySet()){
              boolean isExpire = entry.getValue().isExpire();
            }
            Thread.sleep(10);
          }catch (Exception e){
            e.printStackTrace();
          }
        }
      }
    });
  }

  /**
   * 初始化返回结果容器，requestUniqueKey唯一标示本次调用
   * @param requestUniqueKey
   */
  public static void initResponseData(String requestUniqueKey){
    responseMap.put(requestUniqueKey,UpResponseWrapper.of());
  }

  /**
   * 将异步调用返回的结果放入阻塞队列
   * @param response
   */
  public static void putResultValue(UpResponse response){
    long currentTime = System.currentTimeMillis();
    UpResponseWrapper responseWrapper = responseMap.get(response.getUniqueKey());
    responseWrapper.setResponseTime(currentTime);
    responseWrapper.getResponseQueue().add(response);
  }

  /**
   * 从堵塞队列中获取🈷️异步返回的结果值
   * @param requestUniqueKey
   * @param timeout
   * @return
   */
  public static UpResponse getValue(String requestUniqueKey,long timeout){
    UpResponseWrapper responseWrapper = responseMap.get(requestUniqueKey);
    try {
      return responseWrapper.getResponseQueue().poll(timeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }finally {
      responseMap.remove(requestUniqueKey);
    }
  }

}
