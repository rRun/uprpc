package com.fortis.uprpc.spring.parser;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class  UpRemoteServiceNamespaceHandler extends NamespaceHandlerSupport{
  @Override
  public void init() {
    registerBeanDefinitionParser("service",new ProviderFactoryBeanDefinitionParser());
  }
}
