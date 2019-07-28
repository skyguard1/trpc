package com.skyguard.trpc.client.factory;

import com.skyguard.trpc.client.BaseRpcClient;
import com.skyguard.trpc.client.TrpcClient;
import com.skyguard.trpc.entity.ResponseEntity;

public interface TrpcClientFactory {

    /**
     *
     * @param host
     * @param port
     * @param connectiontimeout
     * @param keepalive
     */
    public TrpcClient getClient(String host, int port) throws Exception;

    /**
     * 创建客户端
     * @param connectiontimeout  连接超时时间
     */
    public void startClient(int connectiontimeout);

    /**
     *
     * @param key
     * @param rpcClient
     */
    public void putRpcClient(String key,BaseRpcClient rpcClient);

    /**
     *
     * @param key
     */
    public void removeRpcClient(String key);

    public boolean containClient(String key);



}
