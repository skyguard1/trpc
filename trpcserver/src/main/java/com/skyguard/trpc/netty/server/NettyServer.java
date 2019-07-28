package com.skyguard.trpc.netty.server;

import com.skyguard.trpc.handler.TrpcServerHandler;
import com.skyguard.trpc.netty.handler.NettyServerHandler;
import com.skyguard.trpc.processor.RpcRequestProcessor;
import com.skyguard.trpc.serialize.KryoDecoder;
import com.skyguard.trpc.serialize.KryoEncoder;
import com.skyguard.trpc.server.TrpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NettyServer implements TrpcServer{

    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    private EventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    private static final int count = 8;






    @Override
    public void registerProcessor(String serviceName, Object serviceInstance) {
        RpcRequestProcessor.registerInstance(serviceName,serviceInstance);
    }

    @Override
    public void stop() throws Exception {
        TrpcServerHandler.clear();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public void start(int port, int timeout) throws Exception {

        ByteBuf byteBuf = Unpooled.copiedBuffer("/t".getBytes());

        bossGroup = new NioEventLoopGroup(count);
        workerGroup = new NioEventLoopGroup(count*2);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR,true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.SO_SNDBUF, 65535)
                .option(ChannelOption.SO_RCVBUF, 65535)
                .childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new DelimiterBasedFrameDecoder(1024,byteBuf));
                pipeline.addLast(new KryoDecoder());
                pipeline.addLast(new KryoEncoder());
                pipeline.addLast(new NettyServerHandler(timeout,port));

            }

        });
        LOG.info("-----------------开始启动--------------------------");
        bootstrap.bind(new InetSocketAddress(port)).sync();
        LOG.info("端口号："+port+"的服务端已经启动");
        LOG.info("-----------------启动结束--------------------------");
    }
}
