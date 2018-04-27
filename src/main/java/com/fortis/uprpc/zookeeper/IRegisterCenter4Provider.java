package com.fortis.uprpc.zookeeper;

import com.fortis.uprpc.model.ProviderService;

import java.util.List;
import java.util.Map;

public interface IRegisterCenter4Provider {

  /**
   * 服务端将服务提供商信息注册到zk对应的节点下
   * @param servicesMetaData
   */
  void registerProvider(final List<ProviderService> servicesMetaData);

  /**
   * 服务端获取服务提供者信息
   * @return key:服务提供者接口；value:服务提供者服务方法列表
   */
  Map<String,List<ProviderService>> getProviderServiceMap();


}
