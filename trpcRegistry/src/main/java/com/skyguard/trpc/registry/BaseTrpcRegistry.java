package com.skyguard.trpc.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.skyguard.trpc.common.TrpcClientType;
import com.skyguard.trpc.entity.HttpRequest;
import com.skyguard.trpc.entity.RpcServer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseTrpcRegistry implements TrpcRegistry{

    private static final Logger LOG = LoggerFactory.getLogger(BaseTrpcRegistry.class);

    private static Map<String,List<RpcServer>> producer = Maps.newConcurrentMap();
    private static List<RpcServer> producerList = Lists.newArrayList();
    private static List<RpcServer> consumerList =Lists.newArrayList();

    @Override
    public void registerService(HttpRequest httpRequest){

        if(isTrpcServer()) {
            String ip = httpRequest.getIp();
            int port = httpRequest.getPort();
            int weight = httpRequest.getWeight();
            RpcServer server = new RpcServer(ip, port);
            server.setWeight(weight);
            String instanceName = httpRequest.getInstanceName();
            LOG.info("register server,ip is{},port is{}",ip,port);
            if (httpRequest.getType() == TrpcClientType.PRODUCER.getCode()) {
                if (!producer.containsKey(instanceName)) {
                    List<RpcServer> servers = Lists.newArrayList();
                    servers.add(server);
                    producer.put(instanceName, servers);
                } else {
                    List<RpcServer> servers = producer.get(instanceName);
                    if (!containsValue(servers,server)) {
                        servers.add(server);
                    }
                }
                if (!containsValue(producerList,server)) {
                    producerList.add(server);
                }
            } else if (httpRequest.getType() == TrpcClientType.CONSUMER.getCode()) {
                if (!containsValue(consumerList,server)) {
                    consumerList.add(server);
                }
            }
        }


    }

    private boolean containsValue(List<RpcServer> servers,RpcServer server){

        boolean flag = false;

        if(CollectionUtils.isNotEmpty(servers)){
            flag = servers.stream().anyMatch(rpcServer->{
                String ip = rpcServer.getIp();
                int port = rpcServer.getPort();
                if(ip.equals(server.getIp())&&port==server.getPort()){
                    return true;
                }
                return false;
            });
        }

        return flag;
    }


    @Override
    public List<RpcServer> getSeverList(HttpRequest httpRequest){

        List<RpcServer> servers = Lists.newArrayList();

        if(isTrpcServer()) {
            String instanceName = httpRequest.getInstanceName();
            if (httpRequest.getType() == TrpcClientType.CONSUMER.getCode()) {
                if (StringUtils.isEmpty(instanceName)) {
                    servers = producerList;
                } else {
                    servers = producer.get(instanceName);
                }
            }
        }

        return servers;
    }

    public List<RpcServer> getProducerList(){
        return producerList;
    }

    public void removeProducer(RpcServer server){
        Set<String> keys = producer.keySet();
        for(String key:keys){
            List<RpcServer> rpcServers = producer.get(key);
            if(containsValue(rpcServers,server)){
                rpcServers.remove(server);
            }
        }

        if(containsValue(producerList,server)){
            producerList.remove(server);
        }

    }

    public boolean isTrpcServer(){
        return false;
    }




}
