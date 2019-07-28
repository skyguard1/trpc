package com.skyguard.trpc.handler;

import com.google.common.collect.Maps;
import com.skyguard.trpc.entity.ResponseEntity;

import java.util.Map;

public class TrpcRequestHandler {

    private static Map<String,ResponseEntity> responseMap = Maps.newConcurrentMap();

    public static void putData(String requestId,ResponseEntity responseEntity){
        responseMap.put(requestId,responseEntity);
    }

    public static ResponseEntity getData(String key){
        return responseMap.get(key);
    }

    public static void remove(String key){
        responseMap.remove(key);
    }




}
