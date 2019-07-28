package com.skyguard.trpc.client;

import com.skyguard.trpc.client.factory.TrpcClientFactory;
import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.common.TrpcRequestType;
import com.skyguard.trpc.entity.RequestEntity;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.exception.TrpcException;
import com.skyguard.trpc.handler.TrpcRequestHandler;
import com.skyguard.trpc.util.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class BaseRpcClient implements TrpcClient{

    private static final Logger LOG = LoggerFactory.getLogger(BaseRpcClient.class);

    private static String connectTimeout = PropertyUtil.getValue(TrpcConfig.CONNECT_TIMEOUT);

    @Override
    public Object invokeImpl(String targetInstanceName, String methodName, String[] argTypes, Object[] args, String token) throws Exception {
        byte[][] argTypeBytes = new byte[argTypes.length][];
        for(int i =0; i < argTypes.length; i++) {
            argTypeBytes[i] =  argTypes[i].getBytes();
        }

        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setRequestId(UUID.randomUUID().toString());
        requestEntity.setInstanceName(targetInstanceName);
        requestEntity.setMethodName(methodName);
        requestEntity.setArgTypes(argTypes);
        requestEntity.setRequestObjects(args);
        requestEntity.setRequestType(TrpcRequestType.GET.getCode());
        requestEntity.setToken(token);

        return invokeImplIntern(requestEntity);
    }

    private Object invokeImplIntern(RequestEntity requestEntity) throws Exception{

        ResponseEntity responseEntity = null;

        int timeout = 2000;
        if(StringUtils.isNotEmpty(connectTimeout)){
            timeout = Integer.parseInt(connectTimeout);
        }

        try {
            sendRequest(requestEntity);
        }catch (Exception e) {
            LOG.error("send request to os sendbuffer error", e);
            throw new RuntimeException("send request to os sendbuffer error", e);
        }

        Object result = null;

        try {
            boolean flag = true;
            long begintime=System.currentTimeMillis();
            while (flag) {
                result = TrpcRequestHandler.getData(requestEntity.getRequestId());
                if (result != null) {
                    flag = false;
                }
                long endtime=System.currentTimeMillis();
                if(endtime-begintime>timeout){//timeout
                    LOG.error("request timeout");
                    flag=false;
                }
            }
        }catch (Exception e){
            LOG.error("receive response timeout ", e);
        }finally {
            TrpcRequestHandler.remove(requestEntity.getRequestId());
        }

        if (result == null) {
            String errorMsg = "receive response timeout("
                    + timeout + " ms),server is: "
                    + getServerIP() + ":" + getServerPort()
                    + " request id is:" + requestEntity.getRequestId();
            throw new TrpcException(errorMsg);
        }

        if(result instanceof ResponseEntity){
            responseEntity = (ResponseEntity) result;

        }else{
            throw new TrpcException("only receive ResponseWrapper or List as response");
        }

        return responseEntity.getResponse();
    }

    public void sendRequest(RequestEntity requestEntity){

    }

    @Override
    public String getServerIP() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public TrpcClientFactory getClientFactory() {
        return null;
    }
}
