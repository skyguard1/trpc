package com.skyguard.trpc.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class InstanceTaskScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceTaskScheduler.class);

    private static  final  int size = 10;

    private static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(size,new ThreadFactoryBuilder().setNameFormat("terekaThread").build());

    private static final int num = 120;

    public static void scheduleInstanceTask(){

        executorService.scheduleAtFixedRate(new RequestCacheTask(),0,num, TimeUnit.SECONDS);



    }


    public static void stop(){

        executorService.shutdown();

    }




}
