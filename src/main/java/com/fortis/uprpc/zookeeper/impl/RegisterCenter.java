package com.fortis.uprpc.zookeeper.impl;

import com.fortis.uprpc.util.IPHelper;
import com.fortis.uprpc.util.PropertyConfigeHelper;
import com.fortis.uprpc.zookeeper.IRegisterCenter4Invoker;
import com.fortis.uprpc.zookeeper.IRegisterCenter4Provider;
import com.fortis.uprpc.model.InvokerService;
import com.fortis.uprpc.model.ProviderService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class RegisterCenter implements IRegisterCenter4Provider,IRegisterCenter4Invoker{

  private static RegisterCenter registerCenter = new RegisterCenter();

  //服务提供者列表,key:服务提供者接口 value：服务提供者方法列表
  private static final Map<String,List<ProviderService>> providerServiceMap
      = Maps.newConcurrentMap();

  //服务提供者zk服务元信息，选择服务（第一次从zk拉取，后面由zk监听器主动更新）
  private static final Map<String,List<ProviderService>> serviceMetaDataMap4Consume
      =Maps.newConcurrentMap();;

  //获取zk注册地址
  private static String ZK_SERVICE = PropertyConfigeHelper.getZkService();

  //获取zk会话超时
  private static int ZK_SESSION_TIME_OUT = PropertyConfigeHelper.getZkConnectionTimeout();

  //获取zk连接超时
  private static int ZK_CONNECTION_TIME_OUT = PropertyConfigeHelper.getZkConnectionTimeout();

  //组装zk的路径
  private static String ROOT_PATH = "/config_register/"+PropertyConfigeHelper.getAppName();
  private static String PROVIDER_TYPE = "/provider";
  private static String INVOKER_TYPE = "/invoker";

  //zk客户端
  private static volatile ZkClient zkClient = null;


  protected RegisterCenter(){

  }

  public static RegisterCenter singleton(){
    return registerCenter;
  }

  @Override
  public void registerProvider(List<ProviderService> servicesMetaData) {
    if (CollectionUtils.isEmpty(servicesMetaData)){
      return;
    }
    //连接zk，注册服务
    synchronized (RegisterCenter.class){
      for (ProviderService provider : servicesMetaData){
        String serviceItfKey = provider.getServiceItf().getName();
        List<ProviderService> providers = providerServiceMap.get(serviceItfKey);
        if (providers == null){
          providers= Lists.newArrayList();
        }
        providers.add(provider);
        providerServiceMap.put(serviceItfKey,providers);
      }
      if (zkClient == null){
        zkClient = new ZkClient(ZK_SERVICE,ZK_SESSION_TIME_OUT,ZK_CONNECTION_TIME_OUT,new SerializableSerializer());
      }
      //创建zk命名空间/当前部署应用app的根目录
      boolean exist = zkClient.exists(ROOT_PATH);
      if (!exist){
        zkClient.createPersistent(ROOT_PATH,true);
      }
      //创建服务提供者节点
      exist = zkClient.exists(ROOT_PATH);
      if (!exist){
        zkClient.createPersistent(ROOT_PATH);
      }
      for (Map.Entry<String,List<ProviderService>> entry : providerServiceMap.entrySet()){
        //创建服务提供者节点
        String serviceNode = entry.getKey();
        String servicePath = ROOT_PATH+"/"+serviceNode+PROVIDER_TYPE;
        exist = zkClient.exists(servicePath);
        if (!exist){
          zkClient.createPersistent(servicePath,true);
        }

        //创建服务提供者节点
        int serverPort = entry.getValue().get(0).getServerPort();
        String localIp = IPHelper.localIp();
        String currentServiceIpNode = servicePath+"/" + localIp + "|" + serverPort;
        exist = zkClient.exists(currentServiceIpNode);
        if (!exist){
          zkClient.createEphemeral(currentServiceIpNode);
        }

        //监听注册服务的变化，同时更新到本地
        zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
          @Override
          public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            if (currentChilds == null){
               currentChilds = Lists.newArrayList();
            }
            List<String> activityServiceIpList =
                Lists.newArrayList(Lists.transform(currentChilds, new Function<String, String>() {
                  @Override
                  public String apply(String input) {
                    return StringUtils.split(input,"|")[0];
                  }
                }));
            refreshActivityService(activityServiceIpList);
          }
        });
      }

    }
  }

  @Override
  public Map<String, List<ProviderService>> getProviderServiceMap() {
    return providerServiceMap;
  }

  @Override
  public void initProviderMap() {
    if (MapUtils.isEmpty(serviceMetaDataMap4Consume)){
      serviceMetaDataMap4Consume.putAll(fetchOrUpdateServiceMetaData());
    }
  }

  @Override
  public Map<String, List<ProviderService>> getServiceMetaDataMap4Consume() {
    return serviceMetaDataMap4Consume;
  }

  @Override
  public void registerInvoker(InvokerService invoker) {
    if (invoker == null)
      return;
    //连接zk
    synchronized (RegisterCenter.class){
      if (zkClient == null){
        zkClient = new ZkClient(ZK_SERVICE,ZK_SESSION_TIME_OUT,ZK_CONNECTION_TIME_OUT,new SerializableSerializer());
      }
      //创建zk命名空间/当前部署应用app的根目录
      boolean exsit = zkClient.exists(ROOT_PATH);
      if (!exsit){
        zkClient.createPersistent(ROOT_PATH,true);
      }
      exsit = zkClient.exists(ROOT_PATH);
      if (!exsit){
        zkClient.createPersistent(ROOT_PATH);
      }
      //创建消费者节点
      String serviceNode = invoker.getServiceItf().getName();
      String servicePath = ROOT_PATH + "/" + serviceNode + INVOKER_TYPE;
      exsit = zkClient.exists(servicePath);
      if (!exsit){
        zkClient.createPersistent(servicePath);
      }

      //创建当前服务器节点
      String localIp = IPHelper.localIp();
      String currentServiceIpNode = servicePath + "/" + localIp;
      exsit = zkClient.exists(currentServiceIpNode);
      if (!exsit){
        zkClient.createEphemeral(currentServiceIpNode);
      }
    }

  }

  //利用zk自动刷新当前的服务提供者列表
  private void refreshActivityService(List<String> serviceIpList){
    if (serviceIpList == null){
      serviceIpList = Lists.newArrayList();
    }

    Map<String ,List<ProviderService>> currentServiceMetaDataMap =
        Maps.newHashMap();

    for (Map.Entry<String,List<ProviderService>> entry : providerServiceMap.entrySet()){
      String key  = entry.getKey();
      List<ProviderService> providerServices = entry.getValue();
      List<ProviderService> serviceMetaDataModelList = currentServiceMetaDataMap.get(key);
      if (serviceMetaDataModelList == null){
        serviceMetaDataModelList = Lists.newArrayList();
      }
      for (ProviderService serviceMetaData : providerServices){
        if (serviceIpList.contains(serviceMetaData.getServerIp())){
          serviceMetaDataModelList.add(serviceMetaData);
        }
      }
      currentServiceMetaDataMap.put(key,serviceMetaDataModelList);
    }
    providerServiceMap.putAll(currentServiceMetaDataMap);
  }

  private Map<String,List<ProviderService>> fetchOrUpdateServiceMetaData(){
    final Map<String,List<ProviderService>> providerServiceMap = Maps.newConcurrentMap();
    //连接zk
    synchronized (RegisterCenter.class){
      if (zkClient == null){
        zkClient = new ZkClient(ZK_SERVICE,ZK_SESSION_TIME_OUT,ZK_CONNECTION_TIME_OUT,new SerializableSerializer());
      }
    }

    //获取服务提供者列表
    String providerPath = ROOT_PATH;
    List<String> providerServices = zkClient.getChildren(providerPath);
    for (String serviceName : providerServices){
      String servicePath = providerPath + "/" + serviceName + PROVIDER_TYPE;
      List<String> ipPathList = zkClient.getChildren(servicePath);
      for (String ipPath : ipPathList){
        String serverIp = StringUtils.split(ipPath,"|")[0];
        String serverPort = StringUtils.split(ipPath,"|")[1];

        List<ProviderService> providerServiceList = getProviderServiceMap().get(serviceName);
        if (providerServiceList == null){
          providerServiceList = Lists.newArrayList();
        }
        ProviderService providerService = new ProviderService();
        try {
          providerService.setServiceItf(ClassUtils.getClass(serviceName));

        } catch (ClassNotFoundException e) {
           throw new RuntimeException(e);
        }
        providerService.setServerIp(serverIp);
        providerService.setServerPort(Integer.parseInt(serverPort));
        providerServiceList.add(providerService);
        providerServiceMap.put(serviceName,providerServiceList);
      }

      //监听注册服务的变化，同时更新数据到本地
      zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
          if (currentChilds == null){
            currentChilds = Lists.newArrayList();
          }
          currentChilds = Lists.newArrayList(Lists.transform(currentChilds, new Function<String, String>() {

            @Override
            public String apply(String input) {
              return StringUtils.split(input,"1")[0];
            }
          }));
          refreshServiceMetaDataMap(currentChilds);
        }
      });
    }
    return providerServiceMap;
  }

  private void refreshServiceMetaDataMap(List<String> serviceIpList){
    if (serviceIpList == null){
      serviceIpList = Lists.newArrayList();
    }

    Map<String ,List<ProviderService>> currentServiceMetaDataMap = Maps.newHashMap();
    for (Map.Entry<String,List<ProviderService>> entry : serviceMetaDataMap4Consume.entrySet()){
      String serviceItfKey = entry.getKey();
      List<ProviderService> serviceList = entry.getValue();
      List<ProviderService> providerServiceList = currentServiceMetaDataMap.get(serviceItfKey);
      if (providerServiceList == null){
        providerServiceList = Lists.newArrayList();
      }
      for (ProviderService serviceMetaData : serviceList){
        if (serviceIpList.contains(serviceMetaData.getServerIp())){
          providerServiceList.add(serviceMetaData);
        }
      }
      currentServiceMetaDataMap.put(serviceItfKey,providerServiceList);
    }
    serviceMetaDataMap4Consume.putAll(currentServiceMetaDataMap);
    //TODO:logger
//    System.out.println();
  }
}
