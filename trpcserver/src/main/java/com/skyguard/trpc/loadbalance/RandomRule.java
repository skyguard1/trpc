package com.skyguard.trpc.loadbalance;

import com.skyguard.trpc.entity.RpcServer;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Random;

public class RandomRule implements IRule{

    private final Random random = new Random();

    @Override
    public RpcServer select(List<RpcServer> servers) {

        if(CollectionUtils.isEmpty(servers)){
            return null;
        }

        if(servers.size()==1){
            return servers.get(0);
        }

        RpcServer server = servers.get(random.nextInt(servers.size()));

        return server;
    }
}
