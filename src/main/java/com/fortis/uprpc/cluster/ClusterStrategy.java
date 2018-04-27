package com.fortis.uprpc.cluster;

import com.fortis.uprpc.model.ProviderService;

import java.util.List;

public interface ClusterStrategy {

  /**
   * 负载均衡算法
   * @param providerServices 服务列表
   * @return 选择的服务
   */
  ProviderService select(List<ProviderService> providerServices);
}
