package com.skyguard.trpc.entity;

public class RpcServer {

    public RpcServer() {
    }

    public RpcServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    private String ip;
    private int port;
    private int weight;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
