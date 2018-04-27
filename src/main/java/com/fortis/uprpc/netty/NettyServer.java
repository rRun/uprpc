package com.fortis.uprpc.netty;

import com.fortis.uprpc.codec.SerializeType;
import com.fortis.uprpc.netty.handler.NettyDecoderHandler;
import com.fortis.uprpc.netty.handler.NettyEncoderHandler;
import com.fortis.uprpc.netty.handler.NettyServerInvokeHandler;
import com.fortis.uprpc.util.PropertyConfigeHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {
  private  static NettyServer nettyServer = new NettyServer();

  //服务端boss线程
  private EventLoopGroup bossGroup;
  //服务端worker线程
  private EventLoopGroup workerGroup;
  //序列化方法类型
  private SerializeType serializeType = PropertyConfigeHelper.getSerializeType();

  private NettyServer(){

  }

  public static NettyServer singleton(){
    return nettyServer;
  }

  public void start(final int port){
    synchronized (this.getClass()){
      if (bossGroup != null || workerGroup != null){
        return;
      }

      bossGroup = new NioEventLoopGroup();
      workerGroup = new NioEventLoopGroup();

      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(bossGroup,workerGroup)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG,1024)
          .childOption(ChannelOption.SO_KEEPALIVE,true)
          .childOption(ChannelOption.TCP_NODELAY,true)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast(new NettyDecoderHandler(AreRequest,serializeType));
              ch.pipeline().addLast(new NettyEncoderHandler(serializeType));
              ch.pipeline().addLast(new NettyServerInvokeHandler());
              //TODO:处理编解码逻辑和服务端业务逻辑vvvbbbbv

            }
          });

      try {
        serverBootstrap.bind(port).sync().channel();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

}
