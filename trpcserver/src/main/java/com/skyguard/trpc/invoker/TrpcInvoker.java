package com.skyguard.trpc.invoker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.skyguard.trpc.cache.RequestCache;
import com.skyguard.trpc.common.TrpcClientType;
import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.entity.HttpRequest;
import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.http.netty.server.NettyHttpServer;
import com.skyguard.trpc.netty.server.NettyServer;
import com.skyguard.trpc.server.TrpcTcpServer;
import com.skyguard.trpc.task.InstanceTaskScheduler;
import com.skyguard.trpc.util.HttpClientUtil;
import com.skyguard.trpc.util.JsonUtil;
import com.skyguard.trpc.util.PropertyUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class TrpcInvoker extends CommonRpcInvoker{

    private static final Logger LOG = LoggerFactory.getLogger(TrpcInvoker.class);

    private NettyServer nettyServer = new NettyServer();

    private NettyHttpServer nettyHttpServer = new NettyHttpServer();

    private static String registerUrl = PropertyUtil.getValue(TrpcConfig.REGISTER_URL);

    private static String getNodeUrl = PropertyUtil.getValue(TrpcConfig.GET_NODE_URL);

    private static boolean isStart = false;

    private static boolean enable = true;

    private static ReentrantLock lock = new ReentrantLock();

    @Override
    public void doExport(int port,int timeout){

        try {
            TrpcTcpServer server = new TrpcTcpServer(nettyServer);
            //TrpcHttpServer server = new TrpcHttpServer(nettyHttpServer);
            server.start(port, timeout);
        }catch (Exception e){
            LOG.error("start server error",e);
        }


    }

    @Override
    public void registerService(String instanceName,RpcServer server,int type){

        try {
            HttpRequest httpRequest = new HttpRequest();
            httpRequest.setInstanceName(instanceName);
            httpRequest.setIp(server.getIp());
            httpRequest.setPort(server.getPort());
            httpRequest.setType(type);
            httpRequest.setWeight(server.getWeight());
            String request = JsonUtil.toJsonString(httpRequest);
            HttpClientUtil.postData(registerUrl,request);
        }catch (Exception e){
            LOG.error("get data error",e);
        }

    }

    public static void clearCache(){

        List<RpcServer> servers = Lists.newArrayList();


        try {
            lock.lock();
            HttpRequest httpRequest = new HttpRequest();
            httpRequest.setInstanceName("");
            httpRequest.setType(TrpcClientType.CONSUMER.getCode());
            String request = JsonUtil.toJsonString(httpRequest);
            String result = HttpClientUtil.postData(getNodeUrl,request);
            if(enable){
                LOG.info("clear cache");
                RequestCache.clear();
            }
            servers = JsonUtil.getObject(result, new TypeReference<List<RpcServer>>() {
            });
            RequestCache.set(servers);
        }catch (Exception e){
            LOG.error("get data error",e);
            enable = false;
        }finally {
            lock.unlock();
        }



    }

    @Override
    public List<RpcServer> fetchFromRegistry(String instanceName){

        List<RpcServer> servers = RequestCache.getServers();

        if(!isStart){
            InstanceTaskScheduler.scheduleInstanceTask();
            isStart = true;
        }

        if(CollectionUtils.isEmpty(servers)) {


            try {
                HttpRequest httpRequest = new HttpRequest();
                httpRequest.setInstanceName(instanceName);
                httpRequest.setType(TrpcClientType.CONSUMER.getCode());
                String request = JsonUtil.toJsonString(httpRequest);
                String result = HttpClientUtil.postData(getNodeUrl, request);
                servers = JsonUtil.getObject(result, new TypeReference<List<RpcServer>>() {
                });
                RequestCache.set(servers);
                enable = true;
            } catch (Exception e) {
                LOG.error("get data error", e);
                enable = false;
            }
        }

        return servers;
    }




}
