package com.fortis.uprpc.util;

import com.fortis.uprpc.codec.SerializeType;

import java.io.InputStream;
import java.util.Properties;

public class PropertyConfigeHelper {

  private static final String PROPERTY_CLASSPATH = "/up.properties";
  private static final Properties properties = new Properties();

  public static String zkService = null;
  public static int zkSessionTimeout;
  public static int zkConnectionTimeout;
  public static SerializeType serializeType;
  public static int channelConnectSize;

  /**
   * 初始化配置
   */
  static {
    InputStream is = null;
    try {
      is = PropertyConfigeHelper.class.getResourceAsStream(PROPERTY_CLASSPATH);
      if (is == null){
        throw new IllegalAccessException("up.properties not found in the classpath");
      }
      properties.load(is);

      zkService = properties.getProperty("zk_service");
      zkSessionTimeout = Integer.parseInt(properties.getProperty("zk_sessionTimeout","500"));
      zkConnectionTimeout = Integer.parseInt(properties.getProperty("zk_connectionTimeout","500"));
      channelConnectSize = Integer.parseInt(properties.getProperty("channel_connect_size","10"));
      String seriType = properties.getProperty("serialize_type");
      serializeType = SerializeType.queryByType(seriType);
      if (serializeType == null){
        throw new RuntimeException("serializeType is null");
      }
    }catch (Exception e){
      throw new RuntimeException(e);
    }finally {
      if (is != null){
        try {
          is.close();
        }catch (Exception e){
          e.printStackTrace();
        }

      }
    }
  }

  public static Properties getProperties() {
    return properties;
  }

  public static String getZkService() {
    return zkService;
  }

  public static int getZkSessionTimeout() {
    return zkSessionTimeout;
  }

  public static int getZkConnectionTimeout() {
    return zkConnectionTimeout;
  }

  public static SerializeType getSerializeType() {
    return serializeType;
  }

  public static int getChannelConnectSize() {
    return channelConnectSize;
  }
}
