package com.skyguard.trpc.client.factory;

import com.skyguard.trpc.client.BaseRpcClient;
import com.skyguard.trpc.client.CommonRpcClient;
import com.skyguard.trpc.client.TrpcClient;
import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.exception.TrpcException;
import com.skyguard.trpc.netty.client.NettyClient;
import com.skyguard.trpc.util.PropertyUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TcpRpcClientFactory implements TrpcClientFactory{

    private static Map<String,TrpcClient> clientMap = new ConcurrentHashMap<>();

    private CommonRpcClient rpcClient = new NettyClient();

    private static String connectTimeout = PropertyUtil.getValue(TrpcConfig.CONNECT_TIMEOUT);



    @Override
    public TrpcClient getClient(String host, int port) throws Exception {
        String key = host+":"+port;
        if(clientMap.containsKey(key)){
            return clientMap.get(key);
        }

        return createClient(host,port);
    }

    private TrpcClient createClient(String host,int port) throws Exception{

        if(rpcClient==null){
            throw new TrpcException("the client is null");
        }

        String key = host+":"+port;

             int timeout = 2000;
             if(StringUtils.isNotEmpty(connectTimeout)){
                 timeout = Integer.parseInt(connectTimeout);
             }

             startClient(timeout);
             TrpcClient client = rpcClient.createClient(host,port);
             clientMap.put(key,client);
             return client;
    }

    @Override
    public void startClient(int connectiontimeout) {

        if(rpcClient!=null){
            rpcClient.startClient(connectiontimeout);
        }

    }

    @Override
    public void putRpcClient(String key, BaseRpcClient rpcClient) {
         clientMap.put(key,rpcClient);
    }

    @Override
    public void removeRpcClient(String key) {
        clientMap.remove(key);
    }

    @Override
    public boolean containClient(String key) {
        return clientMap.containsKey(key);
    }
}
