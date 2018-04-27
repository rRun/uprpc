package com.fortis.uprpc.cluster;

import com.fortis.uprpc.cluster.impl.RandomClusterStrategyImpl;
import com.google.common.collect.Maps;

import java.util.Map;

public class ClusterEngine {
  private static final Map<ClusterStrategyEnum,ClusterStrategy> clusterStrategyMap = Maps.newConcurrentMap();

  static {
    clusterStrategyMap.put(ClusterStrategyEnum.Randow,new RandomClusterStrategyImpl());
  }

  public static ClusterStrategy queryClusterStrategy(String clusterStrategy){
    ClusterStrategyEnum clusterStrategyEnum = ClusterStrategyEnum.queryByCode(clusterStrategy);
    if (clusterStrategyEnum == null){
      new RandomClusterStrategyImpl();
    }
    return clusterStrategyMap.get(clusterStrategyEnum);
  }
}
