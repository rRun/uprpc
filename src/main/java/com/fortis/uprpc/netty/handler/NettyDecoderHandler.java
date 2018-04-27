package com.fortis.uprpc.netty.handler;

import com.fortis.uprpc.codec.SerializeType;
import com.fortis.uprpc.codec.SerializerEngine;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class NettyDecoderHandler extends ByteToMessageDecoder{
  //解码对象class
  private Class<?> genericClass;
  //解码对象编码所使用的编码格式
  private SerializeType serializeType;

  public NettyDecoderHandler(Class<?> genericClass,SerializeType serializeType){
    this.genericClass = genericClass;
    this.serializeType = serializeType;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    //获取消息头所标示的消息体字节数组大小
    if (in.readableBytes() < 4){
      return;
    }
    in.markReaderIndex();
    int dataLength = in.readInt();
    if (dataLength < 0){
      ctx.close();
    }
    //若当前可以获取到的字节数小于实际长度，则直接返回，直到当前可以获取到的字节数等于实际长度
    if (in.readableBytes()<dataLength){
      in.resetReaderIndex();
      return;
    }

    byte[] data = new byte[dataLength];
    in.readBytes(data);
    Object object = SerializerEngine.deserialize(data,genericClass,serializeType.getSerializeType());
    out.add(object);
  }
}
