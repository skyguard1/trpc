package com.skyguard.trpc.serialize;

import com.skyguard.trpc.exception.TrpcException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class BaseEncoder extends MessageToByteEncoder{


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) throws Exception {

        if(obj==null){
            throw new TrpcException("the message is null");
        }

        doEncode(channelHandlerContext,obj,byteBuf);

    }

    public void doEncode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf){

    }


}
