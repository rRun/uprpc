package com.fortis.uprpc.spring;

import com.fortis.uprpc.model.UpRequest;
import com.fortis.uprpc.model.UpResponse;
import com.fortis.uprpc.netty.NettyChannelPoolFactory;
import com.fortis.uprpc.netty.handler.RevokerResponseHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class RevokerServiceCallable implements Callable<UpResponse>{

  private Channel channel;
  private InetSocketAddress inetSocketAddress;
  private UpRequest request;

  public static RevokerServiceCallable of(InetSocketAddress inetSocketAddress,UpRequest request){
    return new RevokerServiceCallable(inetSocketAddress,request);
  }

  public RevokerServiceCallable(InetSocketAddress inetSocketAddress,UpRequest request){
    this.inetSocketAddress = inetSocketAddress;
    this.request = request;
  }

  @Override
  public UpResponse call() throws Exception {
    //初始化返回结果容器，将本次调用的唯一标示作为存入返回结果的map
    RevokerResponseHolder.initResponseData(request.getUniqueKey());
    //根据调用地址返回netty通道
    ArrayBlockingQueue<Channel> blockingQueue = NettyChannelPoolFactory.channelPoolFactoryInstance().acquire(inetSocketAddress);
    try {
      if (channel == null){
        channel = blockingQueue.poll(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);
      }

      while (!channel.isOpen() || !channel.isWritable() ||!channel.isActive()){
        channel = blockingQueue.poll(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);
        if (channel == null){
          NettyChannelPoolFactory.channelPoolFactoryInstance().registerChannel(inetSocketAddress);
        }
      }
      ChannelFuture channelFuture = channel.writeAndFlush(request);
      channelFuture.syncUninterruptibly();

      long invokeTimeout = request.getInvokeTimeout();
      return RevokerResponseHolder.getValue(request.getUniqueKey(),invokeTimeout);
    }catch (Exception e){
      System.out.println("e"+e.getLocalizedMessage());
    }finally {
      NettyChannelPoolFactory.channelPoolFactoryInstance().release(blockingQueue,channel,inetSocketAddress);
    }
    return null;
  }
}
