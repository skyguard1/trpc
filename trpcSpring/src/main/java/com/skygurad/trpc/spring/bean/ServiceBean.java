package com.skygurad.trpc.spring.bean;

import com.skyguard.trpc.annotation.TrpcClient;
import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.config.ServiceConfig;
import com.skyguard.trpc.handler.TrpcRequestHandler;
import com.skyguard.trpc.processor.RpcRequestProcessor;
import com.skyguard.trpc.util.ClassUtil;
import com.skyguard.trpc.util.PropertyUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

public class ServiceBean extends ServiceConfig implements ApplicationContextAware,InitializingBean{

    private String pagckageName = PropertyUtil.getValue(TrpcConfig.PACKAGE_NAME);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

        Set<Class<?>> classes = ClassUtil.getClasses(pagckageName);

        for(Class<?> clazz:classes){
            if(clazz.isAnnotationPresent(TrpcClient.class)) {
                Object bean = applicationContext.getBean(clazz);
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> tClass : interfaces) {
                    String instanceName = tClass.getName();
                    RpcRequestProcessor.registerInstance(instanceName,bean);
                }
            }


        }


    }

    public <T> T getBean(String beanName,Class<T> clazz){
        return (T)applicationContext.getBean(beanName);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        exportService(false);
    }
}
