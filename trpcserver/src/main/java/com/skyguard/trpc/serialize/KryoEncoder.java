package com.skyguard.trpc.serialize;

import com.skyguard.trpc.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class KryoEncoder extends BaseEncoder{


    @Override
    public void doEncode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf){

        byte[] bytes = KryoUtil.serialize(obj);
        ByteBuf byteBuf1 = Unpooled.copiedBuffer(bytes);
        ByteBuf byteBuf2 = Unpooled.copiedBuffer("/t".getBytes());
        ByteBuf req = Unpooled.copiedBuffer(byteBuf1,byteBuf2);
        channelHandlerContext.write(req);
    }



}
