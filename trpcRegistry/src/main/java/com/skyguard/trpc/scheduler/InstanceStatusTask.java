package com.skyguard.trpc.scheduler;

import com.skyguard.trpc.client.BaseRpcClient;
import com.skyguard.trpc.client.TrpcTcpClient;
import com.skyguard.trpc.client.factory.TcpRpcClientFactory;
import com.skyguard.trpc.client.factory.TrpcClientFactory;
import com.skyguard.trpc.common.ResponseStatus;
import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.common.TrpcRequestType;
import com.skyguard.trpc.entity.RequestEntity;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.exception.TrpcException;
import com.skyguard.trpc.handler.BaseTrpcClientHandler;
import com.skyguard.trpc.handler.TrpcClientHandler;
import com.skyguard.trpc.handler.TrpcHttpClientHandler;
import com.skyguard.trpc.registry.CommonRpcRegistry;
import com.skyguard.trpc.util.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class InstanceStatusTask implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(InstanceStatusTask.class);

    private static String clientType = PropertyUtil.getValue(TrpcConfig.CLIENT_TYPE);

    @Override
    public void run() {

        CommonRpcRegistry rpcRegistry = CommonRpcRegistry.getInstance();
        BaseTrpcClientHandler clientHandler = null;
        if(StringUtils.isNotEmpty(clientType)&&clientType.equals("http")){
            clientHandler = new TrpcHttpClientHandler();
        }else {
            clientHandler = new TrpcClientHandler();
        }

        List<RpcServer> rpcServers = rpcRegistry.getProducerList();
        for(RpcServer rpcServer:rpcServers){
            try {
                ResponseEntity responseEntity = clientHandler.sendRequest(rpcServer);
                if(responseEntity==null||responseEntity.getStatus()==ResponseStatus.ERROR.getCode()){
                    throw new TrpcException("response error");
                }
            }catch (Exception e){
                LOG.error("get data error,ip is{},port is{}",rpcServer.getIp(),rpcServer.getPort(),e);
                rpcRegistry.removeProducer(rpcServer);
            }
        }




    }
}
