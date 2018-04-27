package com.fortis.uprpc.model;

import java.io.Serializable;

public class InvokerService implements Serializable {
  private Class serviceItf;


  public Class getServiceItf() {
    return serviceItf;
  }

  public void setServiceItf(Class serviceItf) {
    this.serviceItf = serviceItf;
  }
}
