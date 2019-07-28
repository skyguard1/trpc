package com.skyguard.trpc.invoker;

import com.skyguard.trpc.annotation.TrpcClient;
import com.skyguard.trpc.common.TrpcClientType;
import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.loadbalance.IRule;
import com.skyguard.trpc.proxy.TcpRpcProxy;
import com.skyguard.trpc.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.List;

public class CommonRpcInvoker implements RpcInvoker{

    private static final Logger LOG = LoggerFactory.getLogger(CommonRpcInvoker.class);

    private TcpRpcProxy proxy = new TcpRpcProxy();

    private static String instanceName;

    private static IRule rule;



    @Override
    public void exportService(String instanceName,int weight,int port,int timeout){

        try {
            doExport(port, timeout);
            InetAddress inetAddress = IpUtil.getLocalHostLANAddress();
            RpcServer server = new RpcServer(inetAddress.getHostAddress(),port);
            server.setWeight(weight);
            int type = TrpcClientType.PRODUCER.getCode();
            registerService(instanceName,server,type);
        }catch (Exception e){
            LOG.error("get data error",e);
        }

    }

    public void doExport(int port,int timeout){

    }

    public void registerService(String instanceName,RpcServer server,int type){

    }

    public RpcServer getServer(IRule rule){

        List<RpcServer> rpcServers = fetchFromRegistry(instanceName);

        RpcServer server = null;
        if(rule!=null){
            server = rule.select(rpcServers);
            LOG.info("server ip:"+server.getIp()+",port:"+server.getPort());
        }

        return server;
    }

    @Override
    public <T> T reference(Class<T> tClass,String token){

        try {
            if (tClass.isAnnotationPresent(TrpcClient.class)) {
                TrpcClient trpcClient = tClass.getAnnotation(TrpcClient.class);
                instanceName = trpcClient.name();
                Class<?> clazz = trpcClient.rule();
                rule = (IRule) clazz.newInstance();
            }
        }catch (Exception e){
            LOG.error("get data error",e);
        }



          String className = tClass.getName();
          T result = proxy.getProxyService(tClass,className,rule,token);

          return result;
    }




    public List<RpcServer> fetchFromRegistry(String instanceName){
        return null;
    }






}
