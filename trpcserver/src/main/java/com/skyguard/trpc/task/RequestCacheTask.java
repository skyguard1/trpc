package com.skyguard.trpc.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.skyguard.trpc.cache.RequestCache;
import com.skyguard.trpc.common.TrpcClientType;
import com.skyguard.trpc.common.TrpcConfig;
import com.skyguard.trpc.entity.HttpRequest;
import com.skyguard.trpc.entity.RpcServer;
import com.skyguard.trpc.invoker.TrpcInvoker;
import com.skyguard.trpc.util.HttpClientUtil;
import com.skyguard.trpc.util.JsonUtil;
import com.skyguard.trpc.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RequestCacheTask implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(RequestCacheTask.class);

    @Override
    public void run() {

        TrpcInvoker.clearCache();



    }
}
