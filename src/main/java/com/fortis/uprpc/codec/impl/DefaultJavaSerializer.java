package com.fortis.uprpc.codec.impl;

import com.fortis.uprpc.codec.ISerializer;

import java.io.*;

public class DefaultJavaSerializer implements ISerializer{
  @Override
  public <T> byte[] serialize(T obj) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
      objectOutputStream.writeObject(obj);
      objectOutputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return byteArrayOutputStream.toByteArray();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T deserialize(byte[] data, Class<T> clazz) {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
    try {
      ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
      return (T) objectInputStream.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
