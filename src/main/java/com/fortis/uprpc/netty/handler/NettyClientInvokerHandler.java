package com.fortis.uprpc.netty.handler;

import com.fortis.uprpc.model.UpResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientInvokerHandler extends SimpleChannelInboundHandler{
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    RevokerResponseHolder.putResultValue((UpResponse) msg);
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
