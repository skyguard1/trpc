package com.skyguard.trpc.client.invocation;

import com.skyguard.trpc.client.TrpcClient;
import com.skyguard.trpc.client.factory.TrpcClientFactory;
import com.skyguard.trpc.common.TrpcClientType;
import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.invoker.TrpcInvoker;
import com.skyguard.trpc.loadbalance.IRule;
import com.skyguard.trpc.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

public class BaseClientInvocationHandler implements InvocationHandler{

    private static final Logger LOG = LoggerFactory.getLogger(BaseClientInvocationHandler.class);

    private TrpcInvoker invoker = new TrpcInvoker();

    private IRule rule;

    private String targetInstanceName;

    private String token;



    public BaseClientInvocationHandler(String targetInstanceName, IRule rule,String token){
        this.targetInstanceName = targetInstanceName;
        this.rule = rule;
        this.token = token;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        TrpcClient client = null;


        RpcServer server = invoker.getServer(rule);

        client = getClientFactory().getClient(server.getIp(), server.getPort());
        String methodName = method.getName();
        String[] argTypes = createParamSignature(method.getParameterTypes());
        Object result= client.invokeImpl(targetInstanceName, methodName, argTypes, args, token);


        return result;
    }

    private String[] createParamSignature(Class<?>[] argTypes) {
        if (argTypes == null || argTypes.length == 0) {
            return new String[] {};
        }
        String[] paramSig = new String[argTypes.length];
        for (int x = 0; x < argTypes.length; x++) {
            paramSig[x] = argTypes[x].getName();
        }
        return paramSig;
    }

    public TrpcClientFactory getClientFactory(){
        return null;
    }



}
