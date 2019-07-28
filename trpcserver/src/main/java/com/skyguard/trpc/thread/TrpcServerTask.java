package com.skyguard.trpc.thread;

import com.skyguard.trpc.common.ResponseStatus;
import com.skyguard.trpc.common.TrpcRequestType;
import com.skyguard.trpc.entity.RequestEntity;
import com.skyguard.trpc.entity.ResponseEntity;
import com.skyguard.trpc.handler.TrpcServerHandler;

import java.util.concurrent.Callable;

public class TrpcServerTask implements Callable<ResponseEntity>{

    private RequestEntity requestEntity;

    public TrpcServerTask(RequestEntity requestEntity){
        this.requestEntity = requestEntity;
    }


    @Override
    public ResponseEntity call() throws Exception {

        ResponseEntity responseEntity = null;

        if(requestEntity.getRequestType()== TrpcRequestType.CHECK.getCode()){
            responseEntity = new ResponseEntity();
            responseEntity.setRequestId(requestEntity.getRequestId());
            responseEntity.setStatus(ResponseStatus.SUCCESS.getCode());
            responseEntity.setMessage("success");
        }else if(requestEntity.getRequestType()==TrpcRequestType.GET.getCode()) {
            responseEntity = TrpcServerHandler.handleRequest(requestEntity);
        }


        return responseEntity;
    }
}
