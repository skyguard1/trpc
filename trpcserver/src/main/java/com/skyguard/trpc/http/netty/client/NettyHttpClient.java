package com.skyguard.trpc.http.netty.client;

import com.skyguard.trpc.client.CommonRpcClient;
import com.skyguard.trpc.client.TrpcClient;
import com.skyguard.trpc.client.TrpcHttpClient;
import com.skyguard.trpc.client.TrpcTcpClient;
import com.skyguard.trpc.http.netty.handler.NettyHttpClientHandler;
import com.skyguard.trpc.netty.client.NettyClient;
import com.skyguard.trpc.netty.handler.NettyClientHandler;
import com.skyguard.trpc.serialize.KryoDecoder;
import com.skyguard.trpc.serialize.KryoEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NettyHttpClient implements CommonRpcClient{

    private static final Logger LOG = LoggerFactory.getLogger(NettyHttpClient.class);

    private static final int count = 8;

    private static EventLoopGroup workerGroup = new NioEventLoopGroup(count);

    private final Bootstrap bootstrap = new Bootstrap();




    @Override
    public void startClient(int connectTimeout) {

        // TODO Auto-generated method stub
        LOG.info("----------------客户端开始启动-------------------------------");
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR,true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.SO_SNDBUF, 65535)
                .option(ChannelOption.SO_RCVBUF, 65535);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new HttpResponseDecoder());
                pipeline.addLast(new HttpRequestEncoder());
                pipeline.addLast(new NettyHttpClientHandler());
            }

        });
        LOG.info("----------------客户端启动结束-------------------------------");

    }

    @Override
    public TrpcClient createClient(String host, int port) throws Exception {

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();
        future.awaitUninterruptibly();
        if (!future.isDone()) {
            LOG.error("Create connection to " + host + ":" + port + " timeout!");
            throw new Exception("Create connection to " + host + ":" + port + " timeout!");
        }
        if (future.isCancelled()) {
            LOG.error("Create connection to " + host + ":" + port + " cancelled by user!");
            throw new Exception("Create connection to " + host + ":" + port + " cancelled by user!");
        }
        if (!future.isSuccess()) {
            LOG.error("Create connection to " + host + ":" + port + " error", future.cause());
            throw new Exception("Create connection to " + host + ":" + port + " error", future.cause());
        }
        TrpcHttpClient client = new TrpcHttpClient(future,host,port);
        return client;
    }
}
