package com.skyguard.trpc.http.netty.server;

import com.skyguard.trpc.handler.TrpcServerHandler;
import com.skyguard.trpc.http.netty.handler.NettyHttpServerHandler;
import com.skyguard.trpc.processor.RpcRequestProcessor;
import com.skyguard.trpc.server.TrpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NettyHttpServer implements TrpcServer{

    private static final Logger LOG = LoggerFactory.getLogger(NettyHttpServer.class);

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
                pipeline.addLast(new HttpResponseEncoder());
                pipeline.addLast(new HttpRequestDecoder());
                pipeline.addLast(new NettyHttpServerHandler(timeout));

            }

        });
        LOG.info("-----------------开始启动--------------------------");
        bootstrap.bind(new InetSocketAddress(port)).sync();
        LOG.info("端口号："+port+"的服务端已经启动");
        LOG.info("-----------------启动结束--------------------------");
    }
}
