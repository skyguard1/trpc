package com.skyguard.trpc.thread;

import com.google.common.util.concurrent.FutureCallback;
import com.skyguard.trpc.entity.HttpEntity;
import com.skyguard.trpc.http.netty.handler.NettyHttpServerHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class TrpcHttpServerFuture implements FutureCallback<HttpEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(TrpcHttpServerFuture.class);


    private ChannelHandlerContext ctx;

    public TrpcHttpServerFuture(ChannelHandlerContext ctx){
        this.ctx = ctx;
    }

    @Override
    public void onSuccess(HttpEntity httpEntity) {

        if(ctx.channel().isOpen()&&httpEntity!=null){
            if(httpEntity.getDefaultHttpContent()!=null){
                ctx.write(httpEntity.getResponse());
                ctx.writeAndFlush(httpEntity.getDefaultHttpContent());

            }else{
                ctx.writeAndFlush(httpEntity.getResponse());
            }
        }




    }

    @Override
    public void onFailure(Throwable throwable) {
        LOG.error("server handler fail!", throwable);

        DefaultHttpResponse httpResponse=new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        httpResponse.headers().add(HttpHeaderNames.TRANSFER_ENCODING,
                HttpHeaderValues.CHUNKED);

        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,
                "text/plain; charset=UTF-8");

        httpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        httpResponse.headers().set(HttpHeaderNames.CACHE_CONTROL,"no-cache");
        httpResponse.headers().set(HttpHeaderNames.PRAGMA,"no-cache");
        httpResponse.headers().set(HttpHeaderNames.EXPIRES,"-1");
        DefaultHttpContent defaultHttpContent = new DefaultHttpContent(Unpooled.copiedBuffer(throwable.getMessage(), CharsetUtil.UTF_8));
        HttpEntity httpEntity = new HttpEntity(httpResponse,defaultHttpContent);
        ctx.write(httpEntity.getResponse());
        ctx.writeAndFlush(httpEntity.getDefaultHttpContent());

    }
}
