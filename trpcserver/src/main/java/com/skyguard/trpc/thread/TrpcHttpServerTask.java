package com.skyguard.trpc.thread;

import com.skyguard.trpc.common.ResponseStatus;
import com.skyguard.trpc.common.TrpcRequestType;
import com.skyguard.trpc.entity.HttpEntity;
import com.skyguard.trpc.entity.RequestEntity;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.handler.TrpcServerHandler;
import com.skyguard.trpc.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class TrpcHttpServerTask implements Callable<ResponseEntity>{

    private static final Logger LOG = LoggerFactory.getLogger(TrpcHttpServerTask.class);


    private Object msg;

    public TrpcHttpServerTask(Object msg){
        this.msg = msg;
    }

    @Override
    public ResponseEntity call() throws Exception {

        try{
            ResponseEntity responseEntity = null;
            if (msg instanceof HttpRequest) {
                HttpRequest request = (HttpRequest)msg;
                String uri = request.uri();
                LOG.info("uri:"+uri);
            }
            if (msg instanceof HttpContent) {
                HttpContent httpContent = (HttpContent) msg;
                ByteBuf content = httpContent.content();
                if (content.isReadable()) {
                    String contentMsg=content.toString(CharsetUtil.UTF_8);
                    if(!StringUtils.isEmpty(contentMsg)){
                        RequestEntity requestEntity = JsonUtil.toObject(contentMsg,RequestEntity.class);
                        if(requestEntity.getRequestType()== TrpcRequestType.CHECK.getCode()){
                            responseEntity = new ResponseEntity();
                            responseEntity.setRequestId(requestEntity.getRequestId());
                            responseEntity.setMessage("success");
                            responseEntity.setStatus(ResponseStatus.SUCCESS.getCode());
                        }else
                        responseEntity = TrpcServerHandler.handleRequest(requestEntity);
                        return responseEntity;
                    }



                }
            }
        }catch(Exception e){
            LOG.error("get data error",e);
            return null;
        }finally{
            ReferenceCountUtil.release(msg);
        }
        return null;
    }



}
