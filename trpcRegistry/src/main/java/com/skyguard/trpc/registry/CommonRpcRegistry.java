package com.skyguard.trpc.registry;

import com.skyguard.trpc.annotation.EnableTrpcServer;
import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.util.ClassUtil;
import com.skyguard.trpc.util.PropertyUtil;

import java.util.List;
import java.util.Set;

public class CommonRpcRegistry extends BaseTrpcRegistry{

    private String pagckageName = PropertyUtil.getValue(TrpcConfig.PACKAGE_NAME);

    private CommonRpcRegistry(){

    }

    private static class RegistryHolder{
        public static CommonRpcRegistry INSTANCE = new CommonRpcRegistry();
    }

    public static CommonRpcRegistry getInstance(){
        return RegistryHolder.INSTANCE;
    }

    @Override
    public boolean isTrpcServer(){
        Set<Class<?>> classes = ClassUtil.getClasses(pagckageName);
        boolean flag = classes.stream().anyMatch(tClass->tClass.isAnnotationPresent(EnableTrpcServer.class));

        return flag;
    }



}
