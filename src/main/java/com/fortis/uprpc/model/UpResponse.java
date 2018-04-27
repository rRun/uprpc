package com.fortis.uprpc.model;

import java.io.Serializable;

public class UpResponse implements Serializable {
  //唯一一次标示，UUID
  private String uniqueKey;
  //请求超时时间
  private long invokeTimeout;
  //返回结果对象
  private Object result;

  public String getUniqueKey() {
    return uniqueKey;
  }

  public void setUniqueKey(String uniqueKey) {
    this.uniqueKey = uniqueKey;
  }

  public long getInvokeTimeout() {
    return invokeTimeout;
  }

  public void setInvokeTimeout(long invokeTimeout) {
    this.invokeTimeout = invokeTimeout;
  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }
}
