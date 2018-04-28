package com.fortis.uprpc.zookeeper;

import com.fortis.uprpc.model.InvokerService;
import com.fortis.uprpc.model.ProviderService;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;

/**
 * 服务治理接口
 */
public interface IRegisterCenter4Governance {

  /**
   * 获取服务列表和消费者列表
   * @param serviceName 服务类名
   * @param appKey app唯一标示
   * @return 服务列表和消费者列表
   */
  Pair<List<ProviderService>,List<InvokerService>> queryProvidersAndInvokers(String serviceName,String appKey);
}
