package com.skyguard.trpc.loadbalance;

import com.skyguard.trpc.entity.RpcServer;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class RoundRule implements IRule{

    private static int index = 0;

    @Override
    public RpcServer select(List<RpcServer> servers) {

        if(CollectionUtils.isEmpty(servers)){
            return null;
        }

        if(servers.size()==1){
            return servers.get(0);
        }

        RpcServer server = servers.get(index);
        index++;
        int length = servers.size();
        if(index==length){
            index = 0;
        }

        return server;
    }
}
