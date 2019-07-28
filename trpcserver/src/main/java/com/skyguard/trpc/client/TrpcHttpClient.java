package com.skyguard.trpc.client;

import com.skyguard.trpc.client.factory.TcpRpcClientFactory;
import com.skyguard.trpc.client.factory.TrpcClientFactory;
import com.skyguard.trpc.common.ResponseStatus;
import com.skyguard.trpc.entity.RequestEntity;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.handler.TrpcRequestHandler;
import com.skyguard.trpc.util.JsonUtil;
import com.skyguard.trpc.util.KryoUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;

public class TrpcHttpClient extends BaseRpcClient{

    private static final Logger LOG = LoggerFactory.getLogger(TrpcHttpClient.class);

    private ChannelFuture future;

    private String host;

    private int port;

    public TrpcHttpClient(ChannelFuture future,String host,int port){
        this.future = future;
        this.host = host;
        this.port = port;
    }

    @Override
    public void sendRequest(RequestEntity requestEntity){

        try {

            URI uri = new URI("http://" + host + ":" + port);

            if (future.channel().isOpen()) {
                String msg = JsonUtil.toJsonString(requestEntity);
                DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                        uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));

                // 构建http请求
                request.headers().set(HttpHeaderNames.HOST, host);
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
                ChannelFuture writeFuture = future.channel().writeAndFlush(request);
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
                        } else if (!future.isSuccess()) {
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

        }catch (Exception e){
            LOG.error("get data error",e);
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
