package com.fortis.uprpc.codec;

public interface ISerializer {

  /**
   * 序列化
   * @param obj 序列化对象
   * @param <T> 对象类型范型
   * @return 序列化结果数据
   */
  <T> byte[] serialize(T obj);

  /**
   * 反序列化
   * @param data 序列化结果数据
   * @param clazz 对象类型
   * @param <T> 对象类型范型
   * @return 序列化对象
   */
  <T> T deserialize(byte[] data, Class<T> clazz);
}
