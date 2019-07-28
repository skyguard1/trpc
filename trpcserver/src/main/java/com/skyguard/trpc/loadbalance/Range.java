package com.skyguard.trpc.loadbalance;

import com.skyguard.trpc.entity.RpcServer;

public class Range {

     private RpcServer server;
     private int low;
     private int high;

    public Range(RpcServer server, int low, int high) {
        this.server = server;
        this.low = low;
        this.high = high;
    }

    public RpcServer getServer() {
        return server;
    }

    public void setServer(RpcServer server) {
        this.server = server;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }
}
