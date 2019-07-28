package com.skyguard.trpc.netty.handler;

import com.skyguard.trpc.client.factory.TcpRpcClientFactory;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.exception.TrpcException;
import com.skyguard.trpc.handler.TrpcRequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        if (msg instanceof ResponseEntity) {
            ResponseEntity response = (ResponseEntity) msg;


            TrpcRequestHandler.putData(response.getRequestId(), response);
        } else {
            LOG.error("receive message error,only support List || ResponseWrapper");
            throw new TrpcException("receive message error,only support List || ResponseWrapper");
        }


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        //LOGGER.info(CommonRpcTcpClientFactory.getInstance().containClient(ctx.channel().remoteAddress().toString()));
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
            throws Exception {
        if (!(e.getCause() instanceof IOException)) {
            // only log
            LOG.error("catch some exception not IOException", e);
        }

        if(ctx.channel().isOpen()){
            ctx.channel().close();
        }


    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOG.error("connection closed: " + ctx.channel().remoteAddress());

        if(ctx.channel().isOpen()){
            ctx.channel().close();
        }
    }







}
