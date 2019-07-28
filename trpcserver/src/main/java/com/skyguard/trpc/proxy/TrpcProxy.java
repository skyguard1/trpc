package com.skyguard.trpc.proxy;

import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.loadbalance.IRule;

import java.util.List;

public interface TrpcProxy {

    public <T> T getProxyService(Class<T> clazz, String targetInstanceName,IRule rule,String token);



}
