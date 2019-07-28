package com.skyguard.trpc.handler;

import com.skyguard.trpc.client.BaseRpcClient;
import com.skyguard.trpc.client.TrpcTcpClient;
import com.skyguard.trpc.client.factory.TcpRpcClientFactory;
import com.skyguard.trpc.client.factory.TrpcClientFactory;
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

public class TrpcClientHandler extends BaseTrpcClientHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TrpcClientHandler.class);

    @Override
    public BaseRpcClient getRpcClient(String ip,int port) throws Exception{

        TcpRpcClientFactory clientFactory = new TcpRpcClientFactory();
        TrpcTcpClient client = (TrpcTcpClient) clientFactory.getClient(ip,port);

        return client;
    }





}
