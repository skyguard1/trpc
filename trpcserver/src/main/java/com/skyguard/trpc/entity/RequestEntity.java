package com.skyguard.trpc.entity;

import java.io.Serializable;

public class RequestEntity implements Serializable {

    private static final long serialVersionUID = -3554311529871950375L;

    private String requestId;
    private String instanceName;
    private String methodName;
    private String[] argTypes;
    private Object[] requestObjects;
    private Object message;
    private int requestType;
    private String token;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(String[] argTypes) {
        this.argTypes = argTypes;
    }

    public Object[] getRequestObjects() {
        return requestObjects;
    }

    public void setRequestObjects(Object[] requestObjects) {
        this.requestObjects = requestObjects;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
