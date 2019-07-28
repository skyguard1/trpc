package com.skyguard.trpc.http.netty.handler;

import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.handler.TrpcRequestHandler;
import com.skyguard.trpc.netty.handler.NettyClientHandler;
import com.skyguard.trpc.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyHttpClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(NettyHttpClientHandler.class);


    @Override
     public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                 if (msg instanceof HttpResponse)
                     {
                        HttpResponse response = (HttpResponse) msg;
                        LOG.info("content type:"+response.headers().get(HttpHeaderNames.CONTENT_TYPE));
                     }
                 if(msg instanceof HttpContent)
                     {
                         ByteBuf buf = null;
                         try {
                             HttpContent content = (HttpContent) msg;
                             buf = content.content();
                             String result = buf.toString(CharsetUtil.UTF_8);
                             ResponseEntity responseEntity = JsonUtil.toObject(result,ResponseEntity.class);
                             TrpcRequestHandler.putData(responseEntity.getRequestId(),responseEntity);
                         }catch (Exception e){
                             LOG.error("get data error",e);
                         }finally {
                             buf.release();
                         }
                     }
             }

}
