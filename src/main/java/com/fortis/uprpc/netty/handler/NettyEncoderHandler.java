package com.fortis.uprpc.netty.handler;

import com.fortis.uprpc.codec.SerializeType;
import com.fortis.uprpc.codec.SerializerEngine;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoderHandler extends MessageToByteEncoder{
  //解码对象编码所使用的编码格式
  private SerializeType serializeType;

  public NettyEncoderHandler(SerializeType serializeType){
    this.serializeType = serializeType;
  }
  @Override
  protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
    byte[] data = SerializerEngine.serialize(msg,serializeType.getSerializeType());
    out.writeInt(data.length);
    out.writeBytes(data);
  }
}
