package com.skyguard.trpc.annotation;

import com.skyguard.trpc.common.TrpcClientType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableTrpcClient {

    TrpcClientType type();

    String name();

    int weight() default 0;



}
