package com.fortis.uprpc.spring.parser;

import com.fortis.uprpc.spring.RevokerFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class RevokerFactoryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser{
  @Override
  protected Class<?> getBeanClass(Element element) {
    return RevokerFactoryBean.class;
  }

  @Override
  protected void doParse(Element element, BeanDefinitionBuilder builder) {
    try{
      String timeout = element.getAttribute("timeout");
      String targetInterface =element.getAttribute("interface");
      String clusterStrategy = element.getAttribute("clusterStrategy");
      String remoteAppKey = element.getAttribute("remoteAppKey");
      String groupName = element.getAttribute("groupName");

      builder.addPropertyValue("timeout",timeout);
      builder.addPropertyValue("targetInterface",Class.forName(targetInterface));
      builder.addPropertyValue("remoteAppKey",remoteAppKey);

      if (StringUtils.isNoneBlank(clusterStrategy)){
        builder.addPropertyValue("clusterStrategy",clusterStrategy);
      }
      if (StringUtils.isNoneBlank(groupName)){
        builder.addPropertyValue("groupName",groupName);
      }
    }catch (Exception e){
      throw new RuntimeException(e);
    }
  }
}
