package com.skyguard.trpc.processor;

import com.google.common.collect.Maps;

import java.util.Map;

public class RpcRequestProcessor {

    private static Map<String,Object> instanceMap = Maps.newConcurrentMap();

    public static void registerInstance(String instanceName,Object obj){
        instanceMap.put(instanceName,obj);
    }

    public static Object getObj(String instanceName){
        return instanceMap.get(instanceName);
    }



}
