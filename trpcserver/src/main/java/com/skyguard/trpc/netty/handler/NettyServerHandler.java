package com.skyguard.trpc.netty.handler;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.common.TrpcRequestType;
import com.skyguard.trpc.entity.RequestEntity;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.exception.TrpcException;
import com.skyguard.trpc.thread.TaskExecutor;
import com.skyguard.trpc.thread.TrpcServerFuture;
import com.skyguard.trpc.thread.TrpcServerTask;
import com.skyguard.trpc.util.PropertyUtil;
import com.skyguard.trpc.valid.CommonRpcValidator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.*;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServerHandler.class);

    private static final int PROCESSORS = 5;
    private ExecutorService executorService = new ThreadPoolExecutor(PROCESSORS,PROCESSORS*2,20,TimeUnit.SECONDS,new LinkedBlockingDeque<>(PROCESSORS*2));

    private static String enableSecurity = PropertyUtil.getValue(TrpcConfig.ENABLE_SECURITY);


    private int timeout;

    private int port;

    public NettyServerHandler(int timeout, int port) {
        this.timeout = timeout;
        this.port = port;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelInactive(ctx);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
            throws Exception {
        if (!(e.getCause() instanceof IOException)) {
            // only log
            LOG.error("catch some exception not IOException", e);
        }
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        if (!(msg instanceof RequestEntity) ) {
            LOG.error("receive message error,only support RequestWrapper");
            throw new TrpcException(
                    "receive message error,only support RequestWrapper || List");
        }

        handleRequest(ctx,msg);

    }

    private void handleRequest(ChannelHandlerContext ctx,Object message) {
        try {
            ListeningExecutorService service = TaskExecutor.getInstance().getService();

            try {
                RequestEntity requestEntity = (RequestEntity) message;
                if(requestEntity.getRequestType()==TrpcRequestType.GET.getCode()) {
                    if (StringUtils.isEmpty(enableSecurity) || !enableSecurity.equals("false")) {
                        if (!CommonRpcValidator.isValid(requestEntity.getToken())) {
                            throw new TrpcException("the token is error");
                        }
                    }
                }
                ListenableFuture<ResponseEntity> future = service.submit(new TrpcServerTask(requestEntity));
                if(future.get(timeout, TimeUnit.MILLISECONDS)!=null){
                    Futures.addCallback(future, new TrpcServerFuture(ctx),executorService);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                sendErrorResponse(ctx, (RequestEntity) message,"the server operate CommonRpcRequest timeout,timeout is:"+timeout+",error is"+e.getMessage()+",client Ip is:"+ctx.channel().remoteAddress().toString()+",server Ip:"+getLocalhost());
            }

        } catch (RejectedExecutionException exception) {

            sendErrorResponse(ctx, (RequestEntity) message,"server threadpool full,maybe because server is slow or too many requests"+",server Ip:"+getLocalhost());
        }
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, RequestEntity request,String errorMessage) {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setRequestId(request.getRequestId());
        responseEntity.setMessage(errorMessage);
        ChannelFuture wf = ctx.channel().writeAndFlush(responseEntity);
        wf.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    LOG.error("server write response error,request id is: " + request.getRequestId()+",client Ip is:"+ctx.channel().remoteAddress().toString()+",server Ip:"+getLocalhost());
                    ctx.channel().close();
                }
            }
        });
    }

    private String getLocalhost(){
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            return ip+":"+port;
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("无法获取本地Ip",e);
        }

    }



}
