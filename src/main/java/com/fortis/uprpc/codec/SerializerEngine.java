package com.fortis.uprpc.codec;

import com.fortis.uprpc.codec.impl.DefaultJavaSerializer;
import com.google.common.collect.Maps;

import java.util.Map;

public class SerializerEngine {

  public static final Map<SerializeType,ISerializer> serializerMap = Maps.newConcurrentMap();
  static {
    serializerMap.put(SerializeType.DefaultJavaSerializer,new DefaultJavaSerializer());
  }
  public static Object deserialize(byte[] data,Class<?> genericClass,String serializeType){
    SerializeType serialize = SerializeType.queryByType(serializeType);
    if (serialize == null){
      throw new RuntimeException("serizilize is null");
    }
    ISerializer serializer = serializerMap.get(serialize);
    if (serializer == null){
      throw new RuntimeException("serialize error");
    }
    try {
      return serializer.deserialize(data,genericClass);
    }catch (Exception e){
      throw new RuntimeException(e);
    }
  }

  public static <T> byte[] serialize(T obj,String serializeType){
    SerializeType serialize = SerializeType.queryByType(serializeType);
    if (serialize == null){
      throw new RuntimeException("serizilize is null");
    }
    ISerializer serializer = serializerMap.get(serialize);
    if (serializer == null){
      throw new RuntimeException("serialize error");
    }
    try {
      return serializer.serialize(obj);
    }catch (Exception e){
      throw new RuntimeException(e);
    }
  }
}

