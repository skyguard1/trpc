package com.skyguard.trpc.client.invocation;

import com.skyguard.trpc.client.factory.TcpRpcClientFactory;
import com.skyguard.trpc.client.factory.TrpcClientFactory;
import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.loadbalance.IRule;

import java.util.List;

public class TcpClientInvocationHandler extends BaseClientInvocationHandler{

    public TcpClientInvocationHandler(String targetInstanceName, IRule rule,String token){
        super(targetInstanceName,rule,token);
    }

    @Override
    public TrpcClientFactory getClientFactory(){
        return new TcpRpcClientFactory();
    }



}
