package com.fortis.uprpc.netty.handler;

import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.concurrent.Semaphore;

public class NettyServerInvokeHandler extends SimpleChannelInboundHandler{

  //服务端限流
  private static final Map<String,Semaphore> serviceKeySemaphoreMap = Maps.newConcurrentMap();
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (ctx.channel().isWritable()){

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
