package com.skyguard.trpc.registry;

import com.skyguard.trpc.entity.HttpRequest;
import com.skyguard.trpc.entity.RpcServer;

import java.util.List;

public interface TrpcRegistry {

     public void registerService(HttpRequest httpRequest);

     public List<RpcServer> getSeverList(HttpRequest httpRequest);



}
