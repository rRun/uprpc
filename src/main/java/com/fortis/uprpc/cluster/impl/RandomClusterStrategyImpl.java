package com.fortis.uprpc.cluster.impl;

import com.fortis.uprpc.cluster.ClusterStrategy;
import com.fortis.uprpc.model.ProviderService;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * 负载均衡随机算法实现
 */
public class RandomClusterStrategyImpl implements ClusterStrategy{
  @Override
  public ProviderService select(List<ProviderService> providerServices) {
    int MAX_LEN = providerServices.size();
    int index = RandomUtils.nextInt(0,MAX_LEN-1);
    return providerServices.get(index);
  }
}
