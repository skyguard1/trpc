package com.skygurad.trpc.spring.bean;

import com.google.common.collect.Lists;
import com.skyguard.trpc.annotation.TrpcClient;
import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.util.ClassUtil;
import com.skyguard.trpc.util.PropertyUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Set;

public class ReferenceBean implements BeanDefinitionRegistryPostProcessor,ApplicationContextAware{

    private String pagckageName = PropertyUtil.getValue(TrpcConfig.PACKAGE_NAME);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private List<Class<?>> getReferenceBean(){

        List<Class<?>> classList = Lists.newArrayList();

        Set<Class<?>> classes = ClassUtil.getClasses(pagckageName);
        for(Class<?> clazz:classes){
            if(clazz.isAnnotationPresent(TrpcClient.class)&&clazz.isInterface()){
                classList.add(clazz);
            }
        }

        return classList;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        List<Class<?>> classList = getReferenceBean();
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext)applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        for(Class<?> clazz:classList){
            String[] midArr = clazz.getName().split("[.]");
            String beanName = midArr[midArr.length - 1];
            beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
            RootBeanDefinition beanDefinition = (RootBeanDefinition) beanDefinitionBuilder.getRawBeanDefinition();
            beanDefinition.setBeanClass(ProxyFactory.class);
            beanDefinition.getPropertyValues().add("interfaceClass",clazz.getName());
            beanDefinition.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);

            beanFactory.registerBeanDefinition(beanName,beanDefinition);

            }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
