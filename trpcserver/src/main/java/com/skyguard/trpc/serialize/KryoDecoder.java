package com.skyguard.trpc.serialize;

import com.skyguard.trpc.entity.RequestEntity;
import com.skyguard.trpc.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class KryoDecoder extends BaseDecoder{

    @Override
    public void doDecode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list){

        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        Object obj = KryoUtil.deserialize(bytes);
        list.add(obj);

    }



}
