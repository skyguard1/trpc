package com.skyguard.trpc.proxy;

import com.skyguard.trpc.client.invocation.HttpClientInvocationHandler;
import com.skyguard.trpc.client.invocation.TcpClientInvocationHandler;
import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.loadbalance.IRule;

import java.lang.reflect.Proxy;
import java.util.List;

public class TcpRpcProxy implements TrpcProxy{


    @Override
    public <T> T getProxyService(Class<T> clazz, String targetInstanceName,IRule rule,String token) {

        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[] { clazz },
                new TcpClientInvocationHandler(
                        targetInstanceName, rule,token));
    }
}
