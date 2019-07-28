package com.skyguard.trpc.loadbalance;

import com.skyguard.trpc.entity.RpcServer;

import java.util.List;

public interface IRule {

    public RpcServer select(List<RpcServer> servers);





}
