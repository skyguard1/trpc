package com.skyguard.trpc.client;

public interface CommonRpcClient {

    public void startClient(int connectTimeout);

    public TrpcClient createClient(String host,int port) throws Exception;






}
