package com.skyguard.trpc.client;

import com.skyguard.trpc.client.factory.TcpRpcClientFactory;
import com.skyguard.trpc.client.factory.TrpcClientFactory;
import com.skyguard.trpc.common.ResponseStatus;
import com.skyguard.trpc.entity.RequestEntity;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.handler.TrpcRequestHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class TrpcTcpClient extends BaseRpcClient{

    private static final Logger LOG = LoggerFactory.getLogger(TrpcTcpClient.class);

    private ChannelFuture future;

    public TrpcTcpClient(ChannelFuture future){
        this.future = future;
    }


    @Override
    public void sendRequest(RequestEntity requestEntity){

        // TODO Auto-generated method stub
        if(future.channel().isOpen()){
            ChannelFuture writeFuture = future.channel().writeAndFlush(requestEntity);
            // use listener to avoid wait for write & thread context switch
            writeFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future)
                        throws Exception {
                    if (future.isSuccess()) {
                        return;
                    }
                    String errorMsg = "";
                    // write timeout

                    if (future.isCancelled()) {
                        errorMsg = "Send request to " + future.channel().toString()
                                + " cancelled by user,request id is:"
                                + requestEntity.getRequestId();
                    }else if (!future.isSuccess()) {
                        if (future.channel().isOpen()) {
                            // maybe some exception,so close the channel
                            future.channel().close();
                            getClientFactory().removeRpcClient(future.channel().remoteAddress().toString());
                        }
                        errorMsg = "Send request to " + future.channel().toString() + " error" + future.cause();
                        LOG.error(errorMsg);
                    }
                    ResponseEntity responseEntity = new ResponseEntity();
                    responseEntity.setRequestId(requestEntity.getRequestId());
                    responseEntity.setStatus(ResponseStatus.ERROR.getCode());
                    responseEntity.setMessage(errorMsg);
                    TrpcRequestHandler.putData(responseEntity.getRequestId(), responseEntity);
                }
            });
        }



    }

    @Override
    public String getServerIP() {
        return ((InetSocketAddress) future.channel().remoteAddress()).getHostName();
    }

    @Override
    public int getServerPort() {
        return ((InetSocketAddress) future.channel().remoteAddress()).getPort();
    }

    @Override
    public TrpcClientFactory getClientFactory() {
        return new TcpRpcClientFactory();
    }

    




}
