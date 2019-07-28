package com.skyguard.trpc.serialize;

import com.skyguard.trpc.exception.TrpcException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class BaseDecoder extends ByteToMessageDecoder{


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if(byteBuf==null){
           throw new TrpcException("the byte buffer is null");
        }

        doDecode(channelHandlerContext,byteBuf,list);

    }

    public void doDecode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list){

    }




}
