package com.skyguard.trpc.cache;

import com.google.common.collect.Lists;
import com.skyguard.trpc.entity.RpcServer;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class RequestCache {

     private static List<RpcServer> producerList = Lists.newArrayList();

     public static void set(List<RpcServer> rpcServers){
               producerList = rpcServers;
     }

     public static List<RpcServer> getServers(){
         return producerList;
     }

     public static void clear(){
         producerList.clear();
     }







}
