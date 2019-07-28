package com.skyguard.trpc.loadbalance;

import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.invoker.TrpcInvoker;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ResponseWeightRule implements IRule{

    private static final Logger LOG = LoggerFactory.getLogger(ResponseWeightRule.class);


    private final Random random = new Random();


    @Override
    public RpcServer select(List<RpcServer> servers) {

        if(CollectionUtils.isEmpty(servers)){
            return null;
        }

        if(servers.size()==1){
            return servers.get(0);
        }

        int weight = getAllWeight(servers);
        int length = servers.size();
        if(weight==0){
            return servers.get(random.nextInt(length));
        }

        List<Range> ranges = servers.stream().filter(rpcServer -> rpcServer.getWeight()>0).map(rpcServer -> new Range(rpcServer,1,rpcServer.getWeight())).collect(Collectors.toList());

        for(int i=1;i<ranges.size();i++){
            int low = ranges.get(i-1).getHigh();
            ranges.get(i).setLow(low+1);
            int high = ranges.get(i).getHigh();
            ranges.get(i).setHigh(low+high);
        }

        int num = random.nextInt(weight)+1;

        RpcServer server = null;
        int low = 0;
        int high = ranges.size();

        while (low<high){
            int middle = (low+high)/2;
            if(ranges.get(middle).getLow()<=num&&ranges.get(middle).getHigh()>=num){
                server = servers.get(middle);
                break;
            }else if(ranges.get(middle).getLow()>num){
                high = middle-1;
            }else {
                low = middle+1;
            }
        }

        if(server!=null){
            LOG.info("get server ip:"+server.getIp()+",port:"+server.getPort());
        }else {
            LOG.info("no server found");
        }


        return server;
    }

    private int getAllWeight(List<RpcServer> servers){

        int weight = 0;

        for(int i=0;i<servers.size();i++){
            RpcServer server = servers.get(i);
            weight += server.getWeight();
        }

        return weight;
    }




}
