package com.fortis.uprpc.codec;

import org.apache.commons.lang3.StringUtils;

public enum SerializeType {
  DefaultJavaSerializer("DefaultJavaSerializer")
  ;

  private String serializeType;
  private SerializeType(String serializeType){
    this.serializeType = serializeType;
  }

  public static SerializeType queryByType(String serializeType){
    if (StringUtils.isBlank(serializeType)){
      return null;
    }
    for (SerializeType serialize:SerializeType.values()){
      if (StringUtils.equals(serializeType,serialize.serializeType))
        return serialize;
    }
    return null;
  }

  public String getSerializeType() {
    return serializeType;
  }
}
