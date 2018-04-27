package com.fortis.uprpc.spring.parser;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class UpRemoteReferenceNamespaceHandler extends NamespaceHandlerSupport{
  @Override
  public void init() {
    registerBeanDefinitionParser("reference",new RevokerFactoryBeanDefinitionParser());
  }
}
