package com.fortis.uprpc.cluster;

import org.apache.commons.lang3.StringUtils;

public enum ClusterStrategyEnum {
  Randow("random");

  private String cluster;
  private ClusterStrategyEnum(String cluster){
    this.cluster = cluster;
  }

  public static ClusterStrategyEnum queryByCode(String cluster){
    if (cluster == null)
      return null;

    for (ClusterStrategyEnum temp : ClusterStrategyEnum.values()){
      if (StringUtils.equals(temp.cluster,cluster))
        return temp;
    }
    return null;
  }
}
