package com.skygurad.trpc.spring.bean;

import com.skyguard.trpc.config.ReferenceConfig;
import org.springframework.beans.factory.FactoryBean;

public class ProxyFactory implements FactoryBean{

    private Class<?> interfaceClass;

    private ReferenceConfig referenceConfig = new ReferenceConfig();

    public ProxyFactory() {
    }

    public ProxyFactory(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object getObject() throws Exception {
        return referenceConfig.referenceService(interfaceClass);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
