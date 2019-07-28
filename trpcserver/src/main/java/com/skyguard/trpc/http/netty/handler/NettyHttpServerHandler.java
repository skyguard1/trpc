package com.skyguard.trpc.http.netty.handler;


import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.skyguard.trpc.common.TrpcRequestType;
import com.skyguard.trpc.entity.HttpEntity;
import com.skyguard.trpc.entity.RequestEntity;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.exception.TrpcException;
import com.skyguard.trpc.netty.handler.NettyServerHandler;
import com.skyguard.trpc.thread.*;
import com.skyguard.trpc.util.JsonUtil;
import com.skyguard.trpc.valid.CommonRpcValidator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(NettyHttpServerHandler.class);


    private int timeout;

    public NettyHttpServerHandler(int timeout){
        this.timeout = timeout;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        ListeningExecutorService service = TaskExecutor.getInstance().getService();

        ListenableFuture<ResponseEntity> future = service.submit(new TrpcHttpServerTask(msg));
        try{
            ResponseEntity result=future.get(timeout,TimeUnit.MILLISECONDS);
            if(result!=null){
                String res = JsonUtil.toJsonString(result);
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
                             response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
                             response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                                             response.content().readableBytes());
                if(ctx.channel().isOpen()&&result!=null){
                   ctx.writeAndFlush(response);
                }
            }
        }catch(Exception e){
            DefaultHttpResponse httpResponse=new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            httpResponse.headers().add(HttpHeaderNames.TRANSFER_ENCODING,
                    HttpHeaderValues.CHUNKED);

            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,
                    HttpHeaderValues.APPLICATION_JSON);

            httpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            httpResponse.headers().set(HttpHeaderNames.CACHE_CONTROL,"no-cache");
            httpResponse.headers().set(HttpHeaderNames.PRAGMA,"no-cache");
            httpResponse.headers().set(HttpHeaderNames.EXPIRES,"-1");
            DefaultHttpContent defaultHttpContent = new DefaultHttpContent(Unpooled.copiedBuffer(e.getMessage(), CharsetUtil.UTF_8));
            HttpEntity result =new HttpEntity(httpResponse, defaultHttpContent);
            ctx.write(result.getResponse());
            ctx.writeAndFlush(result.getDefaultHttpContent());
        }




    }




}
