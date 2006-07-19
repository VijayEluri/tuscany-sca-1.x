package org.apache.tuscany.container.spring.config;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SCABeanDefinitionReader extends XmlBeanDefinitionReader {

    private CompositeComponentType componentType;

    public SCABeanDefinitionReader(BeanDefinitionRegistry beanFactory, CompositeComponentType componentType) {
        super(beanFactory);
        this.componentType = componentType;
    }


    protected BeanDefinitionDocumentReader createXmlBeanDefinitionParser() {
        return new SCARootDefinitionParser(componentType);
    }

}
