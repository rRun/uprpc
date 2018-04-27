package com.fortis.uprpc.util;

import com.fortis.uprpc.codec.SerializeType;

public class PropertyConfigeHelper {
  public static String getZkService(){
    return null;
  }

  public static int getZkConnectionTimeout(){
    return 0;
  }

  public static String getAppName(){
    return null;
  }

  public static SerializeType getSerializeType(){
    return SerializeType.DefaultJavaSerializer;
  }

}
