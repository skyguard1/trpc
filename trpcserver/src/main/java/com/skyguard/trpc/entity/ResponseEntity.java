package com.skyguard.trpc.entity;

import java.io.Serializable;

public class ResponseEntity implements Serializable {

    private static final long serialVersionUID = 4590846966635080090L;

    private String requestId;
    private Object response;
    private Integer status;
    private String message;
    private Integer messageLength;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(Integer messageLength) {
        this.messageLength = messageLength;
    }
}
