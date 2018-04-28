package com.fortis.uprpc.util;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

public class IPHelper {

  private static String hostIp = StringUtils.EMPTY;

  /**
   * 获取本机ip
   * @return
   */
  public static String localIp(){
    return hostIp;
  }

  public static String getRealIp(){
    String localIp = null;//本地IP，如果没有配置外网地址则返回它
    String netIp = null;//外网IP
    try {
      Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
      InetAddress ip = null;
      boolean finded = false;//是否找到外网IP
      while (networkInterfaces.hasMoreElements()){
        NetworkInterface ni = networkInterfaces.nextElement();
        Enumeration<InetAddress> addressEnumeration = ni.getInetAddresses();
        while (addressEnumeration.hasMoreElements()){
          ip = addressEnumeration.nextElement();
          if (!ip.isSiteLocalAddress()
              && !ip.isLoopbackAddress()
              && !ip.getHostAddress().contains(":")){
            netIp = ip.getHostAddress();
            finded = true;
            break;
          }else if(ip.isSiteLocalAddress()
              && !ip.isLoopbackAddress()
              && !ip.getHostAddress().contains(":")){
            localIp = ip.getHostAddress();
          }
        }
      }
      if (netIp != null && !"".equals(netIp)){
        return netIp;
      }else
        return localIp;
    }catch (Exception e){
      throw new RuntimeException(e);
    }
  }

  static {
    String ip = null;
    Enumeration allNetInterface;
    try {
      allNetInterface = NetworkInterface.getNetworkInterfaces();
      while (allNetInterface.hasMoreElements()){
        NetworkInterface networkInterface = (NetworkInterface)allNetInterface.nextElement();
        List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
        for (InterfaceAddress add : interfaceAddresses){
          InetAddress Ip = add.getAddress();
          if (Ip != null &&Ip instanceof InetAddress){
            if (StringUtils.equals(Ip.getHostAddress(),"127.0.0.1")){
              continue;
            }
            ip = Ip.getHostAddress();
            break;
          }
        }     }
    }catch (Exception e){
      throw new RuntimeException(e);
    }
    hostIp = ip;
  }
}
