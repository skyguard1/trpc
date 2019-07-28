package com.skyguard.trpc.client;

import com.skyguard.trpc.client.factory.TrpcClientFactory;

public interface TrpcClient {

    public Object invokeImpl(String targetInstanceName, String methodName,
                             String[] argTypes, Object[] args, String token)
            throws Exception;


    public String getServerIP();

    public int getServerPort();


    public TrpcClientFactory getClientFactory();

}
