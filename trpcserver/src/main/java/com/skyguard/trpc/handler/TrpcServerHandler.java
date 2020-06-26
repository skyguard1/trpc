package com.skyguard.trpc.handler;

import com.google.common.collect.Maps;
import com.skyguard.trpc.common.ResponseStatus;
import com.skyguard.trpc.entity.RequestEntity;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.processor.RpcRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

public class TrpcServerHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TrpcServerHandler.class);

    private static Map<String,Method> cacheMethods = Maps.newConcurrentMap();

    public static ResponseEntity handleRequest(RequestEntity request ) {

        ResponseEntity responseWrapper = new ResponseEntity();
        String requestId = request.getRequestId();
        responseWrapper.setRequestId(requestId);
        String targetInstanceName = request.getInstanceName();
        String methodName = request.getMethodName();
        String[] argTypes = request.getArgTypes();
        Object[] requestObjects = null;
        Method method = null;
        try{
            if (argTypes != null && argTypes.length > 0) {
                StringBuilder methodKeyBuilder = new StringBuilder();
                methodKeyBuilder.append(targetInstanceName).append("#");
                methodKeyBuilder.append(methodName).append("$");
                Class<?>[] argTypeClasses = new Class<?>[argTypes.length];
                for (int i = 0; i < argTypes.length; i++) {
                    methodKeyBuilder.append(argTypes[i]).append("_");
                    argTypeClasses[i] = Class.forName(argTypes[i]);
                }
                requestObjects = request.getRequestObjects();
                String methodKey = methodKeyBuilder.toString();
                method = cacheMethods.get(methodKey);
                if(method == null){
                    Class<?> clazz = Class.forName(targetInstanceName);
                    method = clazz.getMethod(methodName, argTypeClasses);
                    cacheMethods.put(methodKey,method);
                }
            } else {
                Class<?> clazz = Class.forName(targetInstanceName);
                method = clazz.getMethod(methodName,
                        new Class<?>[] {});
                if(method == null){
                    throw new Exception("no method: "+methodName+" find in "+targetInstanceName+" on the server");
                }
                requestObjects = new Object[] {};
            }
            Object obj = RpcRequestProcessor.getObj(targetInstanceName);
            method.setAccessible(true);
            responseWrapper.setStatus(ResponseStatus.SUCCESS.getCode());
            responseWrapper.setMessage(ResponseStatus.SUCCESS.getMessage());
            responseWrapper.setResponse(method.invoke(obj, requestObjects));

        }catch(Exception e){
            LOG.error("server handle request error",e);
            responseWrapper.setStatus(ResponseStatus.ERROR.getCode());
            responseWrapper.setMessage(ResponseStatus.ERROR.getMessage());
        }
        return responseWrapper;
    }

    public static void clear(){
        cacheMethods.clear();
    }



}
