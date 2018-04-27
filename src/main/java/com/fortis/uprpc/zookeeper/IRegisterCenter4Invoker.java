package com.fortis.uprpc.zookeeper;

import com.fortis.uprpc.model.InvokerService;
import com.fortis.uprpc.model.ProviderService;

import java.util.List;
import java.util.Map;

public interface IRegisterCenter4Invoker {

  /**
   * 消费端初始化服务提供者信息的本地信息
   */
  void initProviderMap();

  /**
   * 消费端获取服务提供者信息
   * @return 服务提供者信息
   */
  Map<String,List<ProviderService>> getServiceMetaDataMap4Consume();

  /**
   * 注册消费端信息
   * @param invoker 消费端信息
   */
  void registerInvoker(final InvokerService invoker);

}
