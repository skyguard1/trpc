package com.skyguard.trpc.entity;

import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;

import java.io.Serializable;

public class HttpEntity implements Serializable {

    private static final long serialVersionUID = 1066483530993789009L;

    private DefaultHttpResponse response;

    private DefaultHttpContent defaultHttpContent;

    public HttpEntity(DefaultHttpResponse response, DefaultHttpContent defaultHttpContent) {
        this.response = response;
        this.defaultHttpContent = defaultHttpContent;
    }

    public DefaultHttpResponse getResponse() {
        return response;
    }

    public void setResponse(DefaultHttpResponse response) {
        this.response = response;
    }

    public DefaultHttpContent getDefaultHttpContent() {
        return defaultHttpContent;
    }

    public void setDefaultHttpContent(DefaultHttpContent defaultHttpContent) {
        this.defaultHttpContent = defaultHttpContent;
    }
}
