package com.skyguard.trpc.invoker;

public interface RpcInvoker {

    public void exportService(String instanceName,int weight,int port,int timeout);

    public <T> T reference(Class<T> tClass,String token);

}
