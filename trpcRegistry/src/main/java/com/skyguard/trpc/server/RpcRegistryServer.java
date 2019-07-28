package com.skyguard.trpc.server;

import com.skyguard.trpc.client.TrpcClient;
import com.skyguard.trpc.client.factory.TcpRpcClientFactory;
import com.skyguard.trpc.client.factory.TrpcClientFactory;
import com.skyguard.trpc.entity.HttpRequest;
import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.registry.CommonRpcRegistry;
import com.skyguard.trpc.scheduler.InstanceScheduler;

import java.util.List;


public class RpcRegistryServer {

    private CommonRpcRegistry rpcRegistry = CommonRpcRegistry.getInstance();

    public void start(){
        InstanceScheduler.scheduleInstanceTask();
    }

    public void register(HttpRequest httpRequest){
        rpcRegistry.registerService(httpRequest);
    }

    public List<RpcServer> getServerList(HttpRequest httpRequest){
        return rpcRegistry.getSeverList(httpRequest);
    }

    public List<RpcServer> getProducerList(){
        return rpcRegistry.getProducerList();
    }



}
