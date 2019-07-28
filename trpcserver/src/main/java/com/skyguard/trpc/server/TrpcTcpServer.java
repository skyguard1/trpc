package com.skyguard.trpc.server;


public class TrpcTcpServer implements TrpcServer{

    private TrpcServer trpcServer;

    public TrpcTcpServer(TrpcServer trpcServer){
        this.trpcServer = trpcServer;
    }


    @Override
    public void registerProcessor(String serviceName, Object serviceInstance) {
         trpcServer.registerProcessor(serviceName,serviceInstance);
    }

    @Override
    public void stop() throws Exception {
         trpcServer.stop();
    }

    @Override
    public void start(int port, int timeout) throws Exception {
         trpcServer.start(port,timeout);
    }
}
