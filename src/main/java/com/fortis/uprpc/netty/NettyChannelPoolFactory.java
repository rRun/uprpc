package com.fortis.uprpc.netty;

import com.fortis.uprpc.codec.SerializeType;
import com.fortis.uprpc.model.ProviderService;
import com.fortis.uprpc.model.UpResponse;
import com.fortis.uprpc.netty.handler.NettyClientInvokerHandler;
import com.fortis.uprpc.netty.handler.NettyDecoderHandler;
import com.fortis.uprpc.netty.handler.NettyEncoderHandler;
import com.fortis.uprpc.util.PropertyConfigeHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.collections4.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

public class NettyChannelPoolFactory {
  private static final NettyChannelPoolFactory channelPoolFactory= new NettyChannelPoolFactory();

  //key:服务提供者地址 value：netty channel堵塞队列
  private static final Map<InetSocketAddress,ArrayBlockingQueue<Channel> > channelPoolMap = Maps.newConcurrentMap();
  //获取堵塞队列的长度
  private static final int channelConnectSize = PropertyConfigeHelper.getChannelConnectSize();
  //获取序列化类型
  private static final SerializeType serializeType = PropertyConfigeHelper.getSerializeType();
  //获取服务提供者列表
  private List<ProviderService> serviceMetaDataList = Lists.newArrayList();

  private NettyChannelPoolFactory(){

  }

  public static NettyChannelPoolFactory channelPoolFactoryInstance(){
    return channelPoolFactory;
  }

  /**
   * 初始化channel连接队列map
   * @param providerMap
   */
  public void initChannelPoolFactory(Map<String,List<ProviderService>> providerMap){
    Collection<List<ProviderService>> collectionServiceMetaDataList = providerMap.values();
    for (List<ProviderService> serviceMetaDataModels : collectionServiceMetaDataList){
      if (CollectionUtils.isEmpty(serviceMetaDataModels)){
        continue;
      }
      serviceMetaDataList.addAll(serviceMetaDataModels);
    }

    //获取服务提供者地址列表
    Set<InetSocketAddress> socketAddressSet = Sets.newHashSet();
    for (ProviderService serviceMetaData : serviceMetaDataList){
      String serviceIp = serviceMetaData.getServerIp();
      int servicePort = serviceMetaData.getServerPort();

      InetSocketAddress socketAddress = new InetSocketAddress(serviceIp,servicePort);
      socketAddressSet.add(socketAddress);
    }

    //根据服务提供者地址列表初始化channel,并以地址key,channel为value 存入poolMap
    for (InetSocketAddress socketAddress : socketAddressSet){
      try {
        int realChannelConnectSize = 0 ;
        while (realChannelConnectSize < channelConnectSize){
          Channel channel = null;
          while (channel == null){
            //若channel不存在，注册新的channel
            channel =  registerChannel(socketAddress);
          }
          realChannelConnectSize ++;

          ArrayBlockingQueue<Channel> channelArrayBlockingQueue = channelPoolMap.get(socketAddress);
          if (channelArrayBlockingQueue == null){
            channelArrayBlockingQueue = new ArrayBlockingQueue<Channel>(channelConnectSize);
            channelPoolMap.put(socketAddress,channelArrayBlockingQueue);
          }
          channelArrayBlockingQueue.offer(channel);
        }
      }catch (Exception e){
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * 根据服务提供者地址获取对应的channel堵塞队列
   * @param address
   * @return
   */
  public ArrayBlockingQueue<Channel> acquire(InetSocketAddress address){
    return channelPoolMap.get(address);
  }

  /**
   * Channel使用完后，回收到堵塞队列中
   * @param arrayBlockingQueue
   * @param channel
   * @param socketAddress
   */
  public void release(ArrayBlockingQueue<Channel> arrayBlockingQueue,Channel channel,InetSocketAddress socketAddress){
    if (arrayBlockingQueue ==null)
      return;

    if (channel == null || !channel.isActive() || !channel.isOpen() || !channel.isWritable()){
      if (channel != null){
        channel.deregister().syncUninterruptibly().awaitUninterruptibly();
        channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
      }
      Channel newChannel = null;
      while (newChannel == null){
        newChannel = registerChannel(socketAddress);
      }
      arrayBlockingQueue.offer(newChannel);
      return;
    }
    arrayBlockingQueue.offer(channel);
  }

  /**
   * 注册channel
   * @param socketAddress
   * @return
   */
  public Channel registerChannel(InetSocketAddress socketAddress){
    try {
      EventLoopGroup group = new NioEventLoopGroup(10);
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.remoteAddress(socketAddress);
      bootstrap.group(group)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY,true)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast(new NettyEncoderHandler(serializeType));
              ch.pipeline().addLast(new NettyDecoderHandler(UpResponse.class,serializeType));
              ch.pipeline().addLast(new NettyClientInvokerHandler());
            }
          });
      ChannelFuture channelFuture = bootstrap.connect().sync();
      final Channel newChannel = channelFuture.channel();
      final CountDownLatch connectedLatch = new CountDownLatch(1);
      final List<Boolean> isSuccessHodler = Lists.newArrayList();
      channelFuture.addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
          if (future.isSuccess()){
            isSuccessHodler.add(Boolean.TRUE);
          }else {
            future.cause().printStackTrace();
            isSuccessHodler.add(Boolean.FALSE);
          }
          connectedLatch.countDown();
        }
      });
      connectedLatch.await();

      if (isSuccessHodler.get(0))
        return newChannel;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return null;
  }

}
