package com.skyguard.trpc.config;

import com.google.common.collect.Maps;
import com.skyguard.trpc.annotation.EnableTrpcClient;
import com.skyguard.trpc.annotation.TrpcClient;
import com.skyguard.trpc.common.TrpcClientType;
import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.exception.TrpcException;
import com.skyguard.trpc.invoker.RpcInvoker;
import com.skyguard.trpc.invoker.TrpcInvoker;
import com.skyguard.trpc.netty.server.NettyServer;
import com.skyguard.trpc.processor.RpcRequestProcessor;
import com.skyguard.trpc.util.ClassUtil;
import com.skyguard.trpc.util.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class ServiceConfig extends BaseRpcConfig{

    private static final Logger LOG = LoggerFactory.getLogger(ServiceConfig.class);

    private RpcInvoker invoker = new TrpcInvoker();

    private static String instanceName;

    private String pagckageName = PropertyUtil.getValue(TrpcConfig.PACKAGE_NAME);

    private static String connectTimeout = PropertyUtil.getValue(TrpcConfig.CONNECT_TIMEOUT);

    private static String port = PropertyUtil.getValue(TrpcConfig.CONNECT_PORT);

    private static int weight = 0;

    private boolean isProducer(){

        Set<Class<?>> classes = ClassUtil.getClasses(pagckageName);
        if(classes.stream().anyMatch(clazz->clazz.isAnnotationPresent(EnableTrpcClient.class))){
            Class<?> clazz = classes.stream().filter(tClass->tClass.isAnnotationPresent(EnableTrpcClient.class)).findFirst().get();
            EnableTrpcClient enableTrpcClient = clazz.getAnnotation(EnableTrpcClient.class);
            if(enableTrpcClient.type()==TrpcClientType.PRODUCER){
                instanceName = enableTrpcClient.name();
                weight = enableTrpcClient.weight();
                return true;
            }
        }

        return false;
    }

    private Map<Class<?>,Object> getInvokeObj() throws Exception{

        Map<Class<?>,Object> map = Maps.newHashMap();

        Set<Class<?>> classes = ClassUtil.getClasses(pagckageName);
        for(Class<?> tClass:classes){
            if(tClass.isAnnotationPresent(TrpcClient.class)){
                TrpcClient trpcClient = tClass.getAnnotation(TrpcClient.class);
                Class<?> clazz = trpcClient.tCLass();
                Object obj = clazz.newInstance();
                map.put(tClass,obj);
            }
        }

        return map;
    }


    public void exportService(boolean flag){

        try {
            if (!isProducer()) {
                throw new TrpcException("the client is not producer");
            }

            if(flag) {
                Map<Class<?>, Object> map = getInvokeObj();
                registerObj(map);
            }
            int connectPort = 8661;
            if(StringUtils.isNotEmpty(port)){
                connectPort = Integer.parseInt(port);
            }

            int timeout = 2000;
            if(StringUtils.isNotEmpty(connectTimeout)){
                timeout = Integer.parseInt(connectTimeout);
            }
            invoker.exportService(instanceName,weight,connectPort,timeout);


        }catch (Exception e){
            LOG.error("get data error",e);
        }




    }

    public void registerObj(Map<Class<?>,Object> map){

        for(Map.Entry<Class<?>,Object> entry:map.entrySet()){
            Class<?> tClass = entry.getKey();
            Object obj = entry.getValue();
            String className = tClass.getName();
            RpcRequestProcessor.registerInstance(className,obj);
        }
    }







}
