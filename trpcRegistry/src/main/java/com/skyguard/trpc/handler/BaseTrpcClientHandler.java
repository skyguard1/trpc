package com.skyguard.trpc.handler;

import com.skyguard.trpc.client.BaseRpcClient;
import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.common.TrpcRequestType;
import com.skyguard.trpc.entity.RequestEntity;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.util.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class BaseTrpcClientHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTrpcClientHandler.class);

    private static String connectTimeout = PropertyUtil.getValue(TrpcConfig.CONNECT_TIMEOUT);


    public ResponseEntity sendRequest(RpcServer rpcServer) throws Exception{

        int timeout = 2000;
        if(StringUtils.isNotEmpty(connectTimeout)){
            timeout = Integer.parseInt(connectTimeout);
        }

        BaseRpcClient client = getRpcClient(rpcServer.getIp(),rpcServer.getPort());
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setRequestId(UUID.randomUUID().toString());
        requestEntity.setRequestType(TrpcRequestType.CHECK.getCode());
        client.sendRequest(requestEntity);
        Object result = null;
        boolean flag = true;
        long begintime=System.currentTimeMillis();
        while (flag){
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

        ResponseEntity responseEntity = (ResponseEntity) result;
        return responseEntity;
    }

    public BaseRpcClient getRpcClient(String ip,int port) throws Exception{
        return null;
    }


}
