package com.skyguard.trpc.server;


public interface TrpcServer {


    public void registerProcessor(String serviceName,Object serviceInstance);

    public void stop() throws Exception;

    public void start(int port,int timeout) throws Exception;


}
