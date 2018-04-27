package com.fortis.uprpc.spring.parser;

import com.fortis.uprpc.spring.ProviderFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class ProviderFactoryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser{
  @Override
  protected Class<?> getBeanClass(Element element) {
    return ProviderFactoryBean.class;
  }

  @Override
  protected void doParse(Element element, BeanDefinitionBuilder builder) {
    try {
      String serviceItf = element.getAttribute("interface");
      String timeout = element.getAttribute("timeout");
      String serverPort = element.getAttribute("serverPort");
      String ref = element.getAttribute("ref");
      String weight = element.getAttribute("weight");
      String workThreads = element.getAttribute("workThreads");
      String appKey = element.getAttribute("appKey");
      String groupName = element.getAttribute("groupName");

      builder.addPropertyValue("serverPort",serverPort);
      builder.addPropertyValue("timeout",timeout);
      builder.addPropertyValue("serviceItf",Class.forName(serviceItf));
      builder.addPropertyValue("appKey",appKey);
      builder.addPropertyReference("serviceObject",ref);

      if (NumberUtils.isCreatable(weight)){
        builder.addPropertyValue("weight",weight);
      }
      if (NumberUtils.isCreatable(weight)){
        builder.addPropertyValue("workThreads",workThreads);
      }
      if (StringUtils.isNoneBlank(groupName)){
        builder.addPropertyValue("groupName",groupName);
      }
    }catch (Exception e){
      throw new RuntimeException(e);
    }
  }
}
